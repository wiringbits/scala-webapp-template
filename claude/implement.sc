// Orca feature flow — plan a prompt into tasks, implement each, review every
// change against the scoped reviewers in `.orca/reviewers/*.md` and the
// commit-message gate, then open a PR.
//
//   scala-cli run implement.sc -- "Add a <feature> to <area>"
//
// Driven by a plain prompt — no GitHub issue required. The reviewers and the
// commit check are the same ones review.sc uses, so a rule is enforced the same
// way whether code is being reviewed or freshly implemented.
//> using scala 3.10.0
//> using dep "org.virtuslab::orca:0.2.0"
//> using jvm 21
//> using file orca-checks.scala

import orca.{*, given}

// Runs in an isolated worktree (.orca/worktrees/<prompt-hash>) so several
// features can be implemented in parallel without sharing a checkout or branch.
flow(OrcaArgs(args).copy(target = RunTarget.Worktree)):
  val brief =
    s"""$userPrompt
       |
       |Follow the project's conventions: the architecture and setup docs under
       |docs/ (architecture.md, setup-dev-environment.md, design-decisions.md) and
       |the review rules in .orca/reviewers/.
       |
       |While implementing a task, verify with TARGETED tests only: run
       |`sbt "testOnly *TheSpec"` for the specs you touch, or `CI=true sbt
       |Test/compile` to typecheck. Do NOT run the whole `sbt test` suite during a
       |task — a final stage runs it once after the reviews converge.""".stripMargin

  val plan = stage("Plan"):
    Plan.autonomous.from(brief, planningAgent).value

  // One review+fix per task, then a final whole-run loop re-checks everything
  // before the PR. Reviewers run on sonnet; the default selector drops any whose
  // `files:` gate doesn't match. The coder/fixer stays on the default coding
  // agent, where the harder work is.
  val taskOpenFindings =
    for task <- plan.tasks yield
      stage(s"Task: ${task.title}"):
        val session = codingAgent.session("implementer", seed = plan.brief)
        session.run(task.description)
        reviewThenFix(
          coderSession = session,
          reviewers = allReviewers(claude.sonnet),
          task = task,
          checks = List(CommitMessageCheck())
        )

  val finalFixer = codingAgent.session("final-fixer", seed = plan.brief)

  val openFindings = stage("Final review"):
    reviewAndFixLoop(
      coderSession = finalFixer,
      reviewers = allReviewers(claude.sonnet),
      task = Task(Title("The whole planned change"), plan.brief),
      diff = ReviewDiff.WholeRun,
      checks = List(CommitMessageCheck()),
      maxFixTurns = 5,
      priorOpenFindings = taskOpenFindings.flatMap(_.findings)
    )

  // Reviews have converged and only targeted tests ran per task, so run the
  // whole suite once here. The coder session drives it so it can start the
  // Docker/Node env and fix a failure the targeted runs missed; CI re-runs it as
  // the backstop if anything is still red when the PR opens.
  stage("Full test suite"):
    finalFixer.run(
      """Run the full test suite exactly as CI does:
        |`CI=true NODE_OPTIONS=--openssl-legacy-provider sbt test`
        |(start the Docker testcontainers Postgres if needed, and use a Node that
        |accepts --openssl-legacy-provider). If anything fails, fix it with a
        |minimal change and re-run until the whole suite passes; do not touch
        |unrelated code.""".stripMargin
    )

  openPrIfGitHub(
    summarisingAgent = codingAgent,
    openFindings = openFindings
  )

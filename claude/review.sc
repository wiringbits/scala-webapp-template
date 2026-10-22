// Orca review gate — flag code on this branch that breaks the project rules.
//
// Reviews the branch's diff against its base with the reviewers in
// `.orca/reviewers/*.md`, and checks the branch's commit messages. Report-only:
// it prints findings and does not edit anything (fixing a committed branch is
// the job of a normal edit+review cycle, not of this gate).
//
//   scala-cli run review.sc -- "review this branch"
//> using scala 3.10.0
//> using dep "org.virtuslab::orca:0.2.0"
//> using jvm 21
//> using file orca-checks.scala

import orca.{*, given}

flow(OrcaArgs(args)):
  val ctx = summon[FlowContext]
  stage("Review"):
    val base = git.defaultBase() match
      case Right(b) => b
      case Left(_) =>
        throw new OrcaFlowException(
          "no default base branch (origin/HEAD, origin/main, origin/master) " +
            "to diff this branch against"
        )

    val diffText = git.diffVsBase(base)
    if diffText.trim.isEmpty then
      throw new OrcaFlowException(s"nothing to review: no diff against $base")

    val changedFiles =
      """(?m)^\+\+\+ b/(.+)$""".r
        .findAllMatchIn(diffText)
        .map(_.group(1))
        .filterNot(_ == "/dev/null")
        .toList
        .distinct

    val diffRel = ".orca/review-input.diff"
    os.write.over(ctx.workDir / ".orca" / "review-input.diff", diffText)

    // Reviewers on sonnet; the default selector drops any whose `files:` gate
    // doesn't match the branch diff and picks among the rest.
    val report = reviewOnce(
      reviewers = allReviewers(claude.sonnet),
      task = Task(Title("Enforce project rules on this branch"), userPrompt),
      diff = ReviewDiff.InFile(diffRel, changedFiles)
    )

    val commitFindings = CommitMessageCheck().evaluate().findings

    println()
    println(report.render)
    println()
    println(s"## commit-message: ${commitFindings.size} finding(s)")
    commitFindings.foreach: f =>
      println(s"- ${f.title}")
      println(s"  ${f.description}")

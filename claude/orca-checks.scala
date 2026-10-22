// Shared Orca ReviewChecks for this project's flows.
// Included by both review.sc and implement.sc via `//> using file orca-checks.scala`.

import orca.{*, given}

/** Deterministic gate over the commit messages this branch adds. Reviewers only
  * ever see the code diff, so commit rules live here: a `ReviewCheck`'s findings
  * go to the fixer like a reviewer's, and the fixer (full tools) can reword the
  * offending commit.
  *
  * Checks every commit in `defaultBase..HEAD` except orca's own bookkeeping
  * commits (`orca:` subject): on a feature branch those are exactly the commits
  * that become the PR. A git failure is raised, not turned into an empty
  * message — the project rule is to fail loudly, never fabricate a default.
  *
  * It flags only what would be harmful if it reached the final commit — a Claude
  * session link, or a reference to the conversation. It deliberately does NOT
  * require a body or cap the subject length: PRs here squash-merge using the PR
  * title + description, so intermediate commit messages are discarded and the PR
  * body is the real commit body. (In the implement flow those commits are also
  * auto-generated and can't be amended, so body/length rules only produced
  * unfixable findings.)
  */
final class CommitMessageCheck extends ReviewCheck:
  def name: String = "commit-message"

  def evaluate()(using ctx: FlowContext, ev: InStage): ReviewResult =
    val base = ctx.git.defaultBase() match
      case Right(b)  => b
      case Left(_)   => "HEAD~1" // no remote base; fall back to the tip commit
    val res = os
      .proc("git", "log", "--format=%H%x1f%B%x1e", s"$base..HEAD")
      .call(cwd = ctx.workDir, check = false, stderr = os.Pipe)
    if res.exitCode != 0 then
      throw new OrcaFlowException(
        s"commit-message check could not read `git log $base..HEAD`: " +
          res.err.text().trim
      )
    val findings =
      res.out
        .text()
        .split('\u001e')
        .toList
        .map(_.trim)
        .filter(_.nonEmpty)
        .flatMap: entry =>
          entry.split('\u001f') match
            case Array(sha, message) =>
              val subject = message.linesIterator.nextOption().getOrElse("")
              if subject.startsWith("orca:") then Nil // skip orca's own commits
              else messageFindings(sha.take(8), message.trim)
            case _ => Nil
    ReviewResult(findings)

  /** Pure: the rule set over one commit message, so it can be read and tested
    * without running git. `shortSha` only names the commit in the output.
    */
  private def messageFindings(
      shortSha: String,
      message: String
  ): List[ReviewFinding] =
    val lower = message.toLowerCase(java.util.Locale.ROOT)

    def finding(
        title: String,
        description: String,
        suggestion: String
    ): ReviewFinding =
      ReviewFinding(
        title = Title(s"[$shortSha] $title"),
        description = description,
        location = None,
        suggestion = Some(suggestion),
        reopens = None
      )

    // A `Co-Authored-By: Claude` line is allowed; only a session link/footer is
    // not. The claude.ai URL check also catches a "Generated with Claude Code"
    // footer, since that comes with the session URL.
    val leaksSession =
      message.contains("claude.ai/") || message.contains("Claude-Session")

    val sessionReference = Option.when(leaksSession)(
      finding(
        "commit message links the Claude session",
        "The message references the Claude session — a claude.ai link or a " +
          "Claude-Session footer. A commit must not link the session (a " +
          "`Co-Authored-By: Claude` line is fine).",
        "Delete the claude.ai link and any Claude-Session footer, then reword " +
          "the commit."
      )
    )

    val conversationMarkers = List(
      "as requested",
      "as you asked",
      "per our conversation",
      "per your request",
      "you asked",
      "in this session",
      "the user asked",
      "as discussed"
    )

    val conversational = Option.when(conversationMarkers.exists(lower.contains))(
      finding(
        "commit message references the conversation",
        "The message refers to the chat/request (e.g. \"as requested\"). It " +
          "must stand on its own and describe the change, not how it was asked " +
          "for.",
        "Rephrase to describe the change itself; drop any reference to the " +
          "request or conversation."
      )
    )

    List(sessionReference, conversational).flatten

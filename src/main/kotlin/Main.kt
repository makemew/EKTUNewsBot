import app.bot.startBot
import java.io.PrintStream

suspend fun main() {
    System.setOut(PrintStream(System.out, true, Charsets.UTF_8))
    startBot()
}

/* TO DO
*
* 1) to leave contacts of the students community, tg bots, my contact for complaints and suggestions or do a post
* 2) implement paraphrase model and deadline reminder if there is word срок or something like that
* 3) automated photoshop with some simple effects
* 4) change emoji near the bot name to show current weather
* 5) may be return body to preview text again cause if something will happen to gemini api tg channel won't publish any messages
**/
package debugger

case object Assets {
  val banner: String =     " _    __          __                                 \n| |  / /__  _____/ /_____  _________ ___  ____ _____ \n| | / / _ \\/ ___/ __/ __ \\/ ___/ __ `__ \\/ __ `/ __ \\\n| |/ /  __/ /__/ /_/ /_/ / /  / / / / / / /_/ / / / /\n|___/\\___/\\___/\\__/\\____/_/  /_/ /_/ /_/\\__,_/_/ /_/ \n                                                      \n"
  val subheading: String    = "            - processor simulator -                  "
  val subsubheading: String = "            ( press 'h'  for help )                  "
  val help: String = "╔═════╦═══════════════════════════╗\n║ Key ║         Function          ║\n╠═════╬═══════════════════════════╣\n║ r   ║ Run program to completion ║\n║ s   ║ Step through program      ║\n║ h   ║ Show this message         ║\n║ v   ║ Toggle verbose mode       ║\n║ m   ║ Print Memory              ║\n║ p   ║ Print Registers           ║\n║ i   ║ Print cycle status        ║\n║ x   ║ Quit program              ║\n╚═════╩═══════════════════════════╝\n"
}

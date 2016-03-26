package taskey.logic;

/** 
 * @@author A0134177E
 * This class acts as an invoker by executing Commands. It has no knowledge of the specifics of the individual Command
 * objects.
 */
class CommandExecutor {
	
	void execute(Command cmd, LogicMemory logicMemory) throws LogicException {
		cmd.execute(logicMemory);
	}
}

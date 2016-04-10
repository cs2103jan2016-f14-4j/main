package taskey.logic;

/** 
 * @@author A0134177E
 * This class is the base class that requires concrete subclasses to implement the execute() method. The actual
 * implementations of the execute() method will vary between subclasses, depending on which user command they are 
 * specific to.
 */
abstract class Command {
	
	abstract void execute(LogicMemory logicMemory) throws LogicException;
}
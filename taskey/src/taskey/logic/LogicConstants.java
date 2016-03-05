package taskey.logic;

public class LogicConstants {

	public enum ListsID {
		PENDING(0), EXPIRED(1), COMPLETED(2), GENERAL(3), DEADLINE(4), EVENT(5);
		
		private int value;
		
		private ListsID(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}
}

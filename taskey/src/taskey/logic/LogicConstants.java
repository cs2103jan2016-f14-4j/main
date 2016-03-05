package taskey.logic;

public class LogicConstants {

	public enum ListID {
		PENDING(0), EXPIRED(1), COMPLETED(2), GENERAL(3), DEADLINE(4), EVENT(5);
		
		private int value;
		
		private ListID(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}
	
	public enum CategoryID {
		GENERAL(0), DEADLINE(1), EVENT(2), COMPLETED(3);
		
		private int value;
		
		private CategoryID(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}
}

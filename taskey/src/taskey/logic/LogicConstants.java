package taskey.logic;

public class LogicConstants {

	public enum ListsID {
		PENDING(0), COMPLETED(1), EXPIRED(2), ACTION(3);
		private int value;
		private ListsID(int value) {
			this.value = value;
		}
		public int getValue() {
			return value;
		}
	}
	
	
}

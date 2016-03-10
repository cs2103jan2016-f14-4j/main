package taskey.logic;

public class LogicConstants {

	public enum ListID {
		THIS_WEEK(0), PENDING(1), EXPIRED(2), GENERAL(3), DEADLINE(4), EVENT(5), COMPLETED(6);
		
		private int index;
		
		private ListID(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
	}
	
	public enum CategoryID {
		GENERAL(0), DEADLINE(1), EVENT(2), COMPLETED(3);
		
		private int index;
		
		private CategoryID(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
	}
}

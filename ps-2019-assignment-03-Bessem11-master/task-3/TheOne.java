import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

public class TheOne {

	private Object value;
	
	// ALESSIO: This is wrong
	private MasterMild masterMild ;
	
	
	public TheOne(Object v){
		this.value = v;
		this.masterMild = new MasterMild();
	}
	
	public TheOne bind(Method func) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this.value= func.invoke(this.masterMild, this.value);
		return this;
	}
	
	public void printMe() {
		
		Vector<String> messages = (Vector<String>) this.value;
		
		for (String msg : messages) {
			System.out.println(msg);
		}
	}	

}

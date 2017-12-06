package prophet;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONTest01 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("status", 1);
		m.put("message", "ffffffff");
		m.put("data", null);
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println(mapper.writeValueAsString(m));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

package equilinoxmodkit.loader;

import equilinoxmodkit.event.EquilinoxEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/* UNFINISHED Event handler that manages events added by mods and their invocation from Equilinox. */
public class EventManager {
	private static final HashMap<Class<?>, ArrayList<Object[]>> events = new HashMap<>();

	public static void invokeEvents(Class<?> type, EquilinoxEvent event) {
		try {
			if (events.containsKey(type)) {
				for (Object[] objects : events.get(type)) {
					Method method = (Method) objects[0];
					Object obj = objects[1];
					method.invoke(obj, event);
				}
			}
		} catch (IllegalAccessException | InvocationTargetException ignored) {}
	}
	
	
	static void addMethod(Class<?> type, Method method, Object obj) {
		method.setAccessible(true);
		
		if (events.containsKey(type)) {
			events.get(type).add(new Object[] { method, obj });
		} else {
			ArrayList<Object[]> newBatch = new ArrayList<>();
			newBatch.add(new Object[] { method,obj });
			events.put(type, newBatch);
		}
	}
}
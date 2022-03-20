package bri;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Vector;

public class ServiceRegistry {
	// cette classe est un registre de services
	// partag�e en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	static {
		servicesClasses = new Vector<>();
	}
	private static List<Class<? extends Service>> servicesClasses;

	public static void addService(Class<? extends Service> serviceClass) throws ValidationException {
		// verifier la conformite par introspection
		// si non conforme --> exception
		validation(serviceClass);
		//permet de ne pouvoir ajouter qu'un service à la fois sans qu'on ait de conflit
		synchronized (servicesClasses) {
			servicesClasses.add(serviceClass);
		}
	}

	// une methode de validation renvoie void et leve une exception si non validation
	// surtout pas de retour boolean !
	private static void validation(Class<? extends Service> classe) throws ValidationException {
		boolean extendsService = false;
		Constructor<? extends Service> c = null;
		try {
			c = classe.getConstructor(java.net.Socket.class);
		} catch (NoSuchMethodException e) {
			// transformation du type de l'exception quand l'erreur est d�tect�e par ce biais
			throw new ValidationException("Il faut un constructeur avec Socket");
		}
		int modifiers = c.getModifiers();
		if (!Modifier.isPublic(modifiers))
			throw new ValidationException("Le constructeur (Socket) doit etre public");
		if (c.getExceptionTypes().length != 0)
			throw new ValidationException("Le constructeur (Socket) ne doit pas lever d'exception");
	}

	public static Class<? extends Service> getServiceClass(int numService) {
			return servicesClasses.get(numService-1);
	}
	
// toStringue liste les activit�s pr�sentes
	public static String toStringue() {
		String result = "Activites presentes :##";
		int i = 1;
		// foreach n'est qu'un raccourci d'�criture 
		// donc il faut prendre le verrou explicitement sur la collection
		synchronized (servicesClasses) { 
			for (Class<? extends Service> s : servicesClasses) {
				try {
					Method toStringue = s.getMethod("toStringue");
					String string = (String) toStringue.invoke(s);
					result += i + " " + string+"##";
					i++;
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace(); // ??? - normalement deja teste par validation()
				}
			}
		}
		return result;
	}

	//supprimer un service par rapport à son numéro
	public static void supprimerService(int numService) {
		synchronized (servicesClasses) {
			servicesClasses.remove(numService-1);
			System.gc();
		}
	}

	public static void miseAJourService(int numService, Class<? extends Service> nouvelleClasse) throws ValidationException {
		validation(nouvelleClasse);
		synchronized (servicesClasses) {
			servicesClasses.add(numService, nouvelleClasse);
		}
	}

	public static int getServiceIndex(String classeString) {
		for(int i = 0; i < servicesClasses.size(); ++i) {
			if(servicesClasses.get(i).getName().equals(classeString)) {
				return i;
			}
		}

		return -1;
	}
}

/**
 *
 */
package fr.csaurin.swgoh;

/**
 * @author U00I168
 */
public class User {
	private String name;

	private String relativeUrl;

	public User(final String name, final String relativeUrl) {
		this.name = name;
		this.relativeUrl = relativeUrl;
	}

	/**
	 * Permet de récupérer la valeur de l'attribut {@link name}.
	 * @return L'attribut name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Permet de récupérer la valeur de l'attribut {@link relativeUrl}.
	 * @return L'attribut relativeUrl
	 */
	public String getRelativeUrl() {
		return relativeUrl;
	}
}

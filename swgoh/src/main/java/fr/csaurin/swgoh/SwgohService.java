package fr.csaurin.swgoh;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author U00I168
 */
public class SwgohService {

	private static final String SWGOH_GUILD_URL = "https://swgoh.gg/g/19177/the-fire-emblem/";

	private static final String SWGOH_CHARACTERS_URL = "https://swgoh.gg{0}collection/";

	private static final String SWGOH_SHIPS_URL = "https://swgoh.gg{0}ships/";

	private HttpClient httpClient;

	public SwgohService() {
		System.setProperty("javax.net.ssl.trustStore", "C:/PROG_RC/PFD/JDK/1.8.0/jre/lib/security/cacerts");
		System.setProperty("javax.net.ssl.trustAnchors", "C:/PROG_RC/PFD/JDK/1.8.0/jre/lib/security/cacerts");
		httpClient = HttpClientBuilder.create().build();
	}

	public List<User> readUsers() throws IOException {
		List<User> users = new ArrayList<>();

		HttpGet request = new HttpGet(SWGOH_GUILD_URL);
		HttpResponse response = httpClient.execute(request);
		Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));

		for (Element a : doc.select("td a")) {
			String username = a.select("strong").text();
			String userurl = a.attributes().get("href");
			users.add(new User(username, userurl));
		}

		return users;
	}

	public Map<String, Map<String, Integer>> readCharacters(final List<User> users) throws IOException {
		Map<String, Map<String, Integer>> usersCharsStars = new HashMap<>();
		for (User user : users) {
			usersCharsStars.put(user.getName(), readCharacter(user.getRelativeUrl()));
		}
		return usersCharsStars;
	}

	public Map<String, Map<String, Integer>> readShips(final List<User> users) throws IOException {
		Map<String, Map<String, Integer>> usersShipsStars = new HashMap<>();
		for (User user : users) {
			usersShipsStars.put(user.getName(), readShip(user.getRelativeUrl()));
		}
		return usersShipsStars;
	}

	private Map<String, Integer> readCharacter(final String userurl) throws IOException {
		Map<String, Integer> characters = new HashMap<>();

		HttpGet request = new HttpGet(MessageFormat.format(SWGOH_CHARACTERS_URL, userurl));
		HttpResponse response = httpClient.execute(request);
		Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));

		for (Element a : doc.select("div.player-char-portrait a")) {
			String charname = a.select("img").attr("alt");
			int charstars = 7 - a.select("div.star-inactive").size();
			characters.put(charname, charstars);
		}

		for (Element img : doc.select("div.char-portrait-image img")) {
			String charname = img.attr("alt");
			characters.put(charname, 0);
		}

		return characters;
	}

	private Map<String, Integer> readShip(final String userurl) throws IOException {
		Map<String, Integer> ships = new HashMap<>();

		HttpGet request = new HttpGet(MessageFormat.format(SWGOH_SHIPS_URL, userurl));
		HttpResponse response = httpClient.execute(request);
		Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));

		for (Element a : doc.select("div.player-ship-portrait a")) {
			String shipname = a.select("img").attr("alt");
			int shipstars = 7 - a.select("div.ship-portrait-full-star-inactive").size();
			ships.put(shipname, shipstars);
		}

		for (Element img : doc.select("div.ship-portrait img")) {
			String shipname = img.attr("alt");
			ships.put(shipname, 0);
		}

		return ships;
	}

	// Debug method
	public void printCsv(final Map<String, Map<String, Integer>> usersCharsStars) {
		List<String> charnames = new ArrayList<>(usersCharsStars.get("Leguman").keySet());
		Collections.sort(charnames);

		System.out.print("user;");
		for (String charname : charnames) {
			System.out.print(charname + ";");
		}
		System.out.println();

		for (Map.Entry<String, Map<String, Integer>> entry : usersCharsStars.entrySet()) {
			System.out.print(entry.getKey() + ";");
			for (String charname : charnames) {
				System.out.print(entry.getValue().get(charname) + ";");
			}
			System.out.println();
		}

	}

}

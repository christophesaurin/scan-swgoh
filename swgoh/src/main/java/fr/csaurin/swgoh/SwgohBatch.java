package fr.csaurin.swgoh;

import java.util.List;
import java.util.Map;

/**
 * @author U00I168
 */
public class SwgohBatch {

	public static void main(final String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage : SwgohBatch spreadsheetId");
			System.exit(-1);
		}
		System.out.println("Initialisation du batch");
		String spreadsheetId = args[0];
		SwgohService scanSwgoh = new SwgohService();
		SpreadsheetService googleSpreadsheet = new SpreadsheetService();

		System.out.println("Lecture des joueurs inscrits à swgoh");
		List<User> users = scanSwgoh.readUsers();
		System.out.println("Lecture des personnages des joueurs");
		Map<String, Map<String, Integer>> usersCharsStars = scanSwgoh.readCharacters(users);
		System.out.println("Lecture des vaisseaux des joueurs");
		Map<String, Map<String, Integer>> usersShipsStars = scanSwgoh.readShips(users);

		System.out.println("Mise à jour du fichier google");
		googleSpreadsheet.updateFile(spreadsheetId, usersCharsStars, usersShipsStars);
		System.out.println("Traitement terminé");
	}

}

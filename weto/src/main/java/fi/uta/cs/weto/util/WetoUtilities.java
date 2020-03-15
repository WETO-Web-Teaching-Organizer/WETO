package fi.uta.cs.weto.util;

import com.google.common.net.HttpHeaders;
import fi.uta.cs.weto.model.WetoActionException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;

public class WetoUtilities
{
  /* Definitions for using student anynomization
  private static final String[] randomFirstNames =
  {
    "Forrest", "Georgianne", "Agnes", "Vonda", "Kaci", "Shantel", "Chana",
    "Towanda", "Tommye", "Talia", "Easter", "Julissa", "Ayako", "Kristie",
    "Janay", "Luann", "Shera", "Nelia", "Shala", "Demetra", "Regan", "Bert",
    "Ludivina", "Kimberely", "Tennie", "Criselda", "Maddie", "Holley", "Tisha",
    "Bo", "Aurelio", "Cornelia", "Blaine", "Rosalie", "Harris", "Myrtis", "Ian",
    "Viki", "Raeann", "Frederica", "Etha", "Wayne", "Vicenta", "Rhoda", "Daron",
    "Ezra", "Hiedi", "Dona", "Lavette", "Eugenie", "Rex", "Angela", "Annette",
    "Carin", "Earlean", "Lorie", "Ileana", "Greta", "Edwardo", "Eliana", "Mayme",
    "Meta", "Kay", "Ninfa", "Rebecka", "Elwood", "Yu", "Corey", "Adam",
    "Arletha", "Janise", "Gilma", "Ouida", "Dotty", "Mac", "Myrtie", "Melony",
    "Wanetta", "Melia", "Caryn", "Precious", "Iva", "Eleonor", "Bianca",
    "Deidre", "Delois", "Domitila", "Sherrie", "Christy", "Lakisha", "Darren",
    "Titus", "Yuri", "Alisa", "Bennie", "Azucena", "Ginette", "Warren",
    "Eufemia", "Keva", "Ileen", "Deana", "Clarence", "Kandis", "Williemae",
    "Tera", "Tova", "Edna", "Carmine", "Leisa", "Genna", "Christeen", "Franklin",
    "Karla", "Adolfo", "Charla", "Vannessa", "Keturah", "Robbie", "Classie",
    "Lillian", "Jannet", "Merna", "Brittanie", "Kristine", "Alex", "Billie",
    "Isis", "Samella", "Jeni", "Lavonne", "Kaylee", "Makeda", "Melva", "Virgen",
    "Dillon", "Jenice", "Izola", "Gil", "Deloras", "Reed", "Milagros", "Yael",
    "Brandi", "Yetta", "Bart", "Hosea", "Nikita", "Novella", "Clora", "Floria",
    "Myesha", "Celsa", "Mabel", "Vikki", "Opal", "Nobuko", "Omer", "Lynetta",
    "Keren", "Sunni", "Bula", "Genia", "Cheryle", "Lynne", "Evon", "Evangelina",
    "Bao", "Denae", "Kirstin", "Barabara", "Stacy", "Lenard", "Raylene",
    "Alysia", "Genny", "Albertha", "Shanti", "Frida", "Jerald", "Keena", "Mindi",
    "Dionne", "Gearldine", "Katrina", "Bryon", "Ernestine", "Marchelle", "Malik",
    "Una", "Kimbery", "Stephany", "Sunny", "Alvina", "Norine", "Mable",
    "Shawnee", "Dannie", "Sydney", "Blair", "Nathan", "Latricia", "Eric",
    "Rasheeda", "Hettie", "Homer", "Marketta", "Dirk", "Hortencia", "Eura",
    "Jaqueline", "Junita", "Lindsay", "Vanesa", "Mohamed", "Annetta", "Abel",
    "Linsey", "Shonta", "Emerson", "Mi", "Alisia", "Tajuana", "Zoila", "Johnnie",
    "Moriah", "Isa", "Dottie", "Stan", "Nicky", "Jeramy", "Tyra", "Francene",
    "Percy", "Solange", "Serina", "Alberto", "Magaly", "Jessenia", "Hue",
    "Bethany", "Marhta", "Paige", "Kent", "Ocie", "Arnette", "Mila", "Nguyet",
    "Nicholas", "Katharyn", "Elliot", "Lewis", "Lucas", "Malisa", "Babara",
    "Willard", "Daisey", "Louisa", "Eun", "Patricia", "Renna", "Williams",
    "Maxwell", "Thea", "Andre", "Fredric", "Eula", "Jerilyn", "Andrea", "Elna",
    "Alise", "Elvia", "Harmony", "Devorah", "Merrie", "Dorene", "Valery",
    "Chauncey", "Hilaria", "Krystin", "Jasper", "Krissy", "Janae", "Kathaleen",
    "Wilson", "Tandra", "Sterling", "Linette", "Elza", "Aundrea", "Molly",
    "Iesha", "Latoria", "Kate", "Rolf", "George", "Christena", "Detra", "Lucien",
    "Erwin", "Shirleen", "Eloisa", "Eda", "Adena", "Venice", "Eleanore",
    "Bailey", "Oliva", "Mirian", "Maxima", "Rochelle", "Zandra", "Tandy",
    "Kristy", "Jeneva", "Lyndsay", "Suzan", "Vida", "Norris", "Sherell", "Derek",
    "Tameika", "Kira", "Josette", "Lesia", "Leonila", "Horace", "Dorine",
    "Larisa", "Sarah", "Laine", "Velma", "Irmgard", "Valentin", "Lanelle",
    "Abigail", "Korey", "Eliz", "Temika", "Natalia", "Tamar", "Allan", "Garnett",
    "Betsy", "Melinda", "Tereasa", "Noah", "Elenora", "Shannan", "Lavone",
    "Letitia", "Carma", "Charlotte", "Arnita", "Eneida", "Lyndon", "Tara",
    "Clementine", "Minnie", "Murray", "Janina", "Nicola", "Octavia", "Griselda",
    "Jeremy", "Alysa", "Kyle", "Marline", "Yaeko", "Joseph", "Vesta", "Tora",
    "Leana", "Ressie", "Delicia", "Gaye", "Yolando", "Karina", "Leota",
    "Renetta", "Frank", "Kenny", "Marge", "Keesha", "Velva", "Reyna", "Justina",
    "Neva", "Lawanna", "Suzann", "Willa", "Tomiko", "Virgie", "Geri", "Lashell",
    "Johnetta", "Bradly", "Adriene", "Cassaundra", "Louanne", "Hazel",
    "Priscilla", "Wendell", "Eulah", "Willian", "Noelia", "Stella", "Shanon",
    "Librada", "Kellee", "Casie", "Sharolyn", "Shandi", "Callie", "Debera",
    "Dominique", "Tawanna", "Alessandra", "Leonora", "Gary", "Trina", "Lacy",
    "Xavier", "Andra", "Jutta", "Charlesetta", "Halley", "Chin", "Marcell",
    "Lakeisha", "Cathrine", "Daina", "Jani", "Deb", "Elfreda", "Harley",
    "Shawanna", "Hugh", "Lucius", "Nida", "Berenice", "Tanya", "Chong", "Shane",
    "Sherika", "Elmer", "Carmela", "Reita", "Racheal", "Sage", "Mariel",
    "Leonardo", "Kindra", "Jan", "Emely", "Sue", "Ivan", "Merrill", "Royce",
    "Jovita", "Josefa", "Laura", "Lela", "Dani", "Esther", "Sumiko", "Naida",
    "Gertrudis", "Brice", "Anjelica", "Jeniffer", "Mia", "Carri", "Suellen",
    "Milford", "Barb", "Weldon", "Marcelene", "Lakia", "Renea", "Merry",
    "Tammie", "Hester", "Natisha", "Krystle", "Layne", "Shaquana", "William",
    "Lorina", "Sina", "Dania", "Garrett", "Marta", "Kimberli", "Tamela",
    "Estrella", "Lashawn", "Michel", "Leah", "Laurice", "Ella", "Demarcus",
    "Myrle", "Janey", "Leonie", "Carmen", "Donna", "Shanelle", "Mayra", "Malika",
    "Freeman", "Zelma", "Jennifer", "Lekisha", "Consuela", "Joanne", "Chiquita",
    "Patience", "Letha", "Araceli", "Kerrie", "Bryant", "Annamae", "Ray",
    "Shara", "Larraine", "Shan", "Hayley", "Enriqueta", "Bertram", "Yasmine",
    "Daphne", "Lenita", "Vince", "Michiko", "Anthony", "Neville", "Loise",
    "Agustin", "Porsche", "Diann", "Berneice", "Johna", "Edra", "Wm", "Estell",
    "Latrina", "Nigel", "Many", "Roxane", "Wilbert", "Nevada", "Sheryl", "Lula",
    "Refugio", "Orlando", "Rafael", "Christiane", "Ali", "Felica", "Glenna",
    "Cameron", "Gabriela", "Cristine", "Hubert", "Mei", "Nicolle", "Nedra",
    "Hilary", "Pa", "Tanner", "Deborah", "Kathryne", "Herlinda", "Sheri",
    "Summer", "Arnetta", "Tricia", "Chase", "Rick", "Jc", "Alona", "Rachael",
    "Martine", "Synthia", "Rita", "Mozell", "Grant", "Hunter", "Elenore",
    "Raguel", "Deadra", "Deann", "Yoko", "Burl", "Osvaldo", "Danyelle", "Wan",
    "Carey", "Leoma", "Kiersten", "Thi", "Londa", "Carlos", "Brunilda",
    "Francisca", "Sybil", "Ahmed", "Gala", "Stephan", "Yajaira", "Bee", "Larue",
    "Reva", "Lorean", "Aracely", "Larry", "Gayla", "Myung", "Grazyna", "Milo",
    "Britany", "Sabine", "Graham", "Cecelia", "Odessa", "Pamelia", "Bethann",
    "Gabriella", "Robbi", "Delfina", "Malka", "Karl", "Macie", "Klara", "Jada",
    "Taisha", "Wendi", "Rosanne", "Claris", "Deena", "Evita", "Michaele",
    "Beatriz", "Verena", "Lannie", "Hattie", "Cassidy", "Gayle", "Dacia",
    "Inell", "Cesar", "Chieko", "Nestor", "Wilma", "Gemma", "Bobby", "Carline",
    "Renay", "Tran", "Laci", "Bernetta", "Christine", "Jules", "Toby", "Elvis",
    "Tiffani", "Jacquiline", "Miesha", "Marcelino", "Sherryl", "Tabetha",
    "Olimpia", "Harold", "Collette", "Brittney", "Harrison", "Rudolf", "Nisha",
    "Glady", "Shanika", "Lili", "Adela", "Dortha", "Flossie", "Alla", "Desire",
    "Fredricka", "Terrie", "Nelson", "Spring", "Randi", "Salvador", "Leonard",
    "Loria", "Jonah", "Romeo", "Diana", "Roseanne", "Wallace", "Cherish",
    "Rolande", "Reuben", "Jeffery", "Lyndsey", "Stepanie", "Lucila", "Kendra",
    "Brittani", "Lauri", "Tiera", "Kam", "Demetrice", "Denver", "Refugia",
    "Clementina", "Tiny", "Lonny", "Tiara", "Timmy", "Shay", "Van", "Evelyn",
    "Aide", "Estefana", "Hershel", "Kraig", "Assunta", "Katherina", "Dreama",
    "Deanne", "Rae", "Mirta", "Maybell", "Arnulfo", "Kimberlie", "Luba",
    "Brooks", "Tu", "Lyn", "Silva", "Ladawn", "Jacquelyn", "Ehtel", "Trista",
    "Jeanelle", "Sherice", "Majorie", "Abbey", "Travis", "Leif", "Rayna", "Asia",
    "Arminda", "Lance", "Salley", "Wei", "Hyun", "Slyvia", "Vivan", "Mechelle",
    "Amado", "Alyce", "Hang", "Debroah", "Marcie", "Von", "Stacia", "Melani",
    "Inge", "Faustina", "Launa", "Floretta", "Lauran", "Velda", "Lisette",
    "Lakesha", "Margot", "Bella", "Danilo", "Wendy", "Lauretta", "Shelton",
    "Junie", "Rod", "Rebeca", "Kandy", "Leila", "Alexa", "Hollie", "Sadie",
    "Jacquelin", "Neda", "Lucia", "Ted", "Kris", "Taneka", "Danial", "Shaunna",
    "Filiberto", "Odilia", "Mandy", "Carlo", "Lera", "Briana", "Sandie", "Arica",
    "Genevie", "Jade", "Myrta", "Thanh", "Jill", "Mitchell", "Chandra", "Nakita",
    "Rosamaria", "Roxann", "Yoshiko", "Hallie", "Alina", "Lamonica", "Damian",
    "Dian", "Rachel", "Sylvester", "Jarrett", "Edda", "Ashlea", "Loura",
    "Albert", "Britni", "Zachariah", "Versie", "Arlene", "Janine", "Erlinda",
    "Elvera", "Brigid", "Yvone", "Gregory", "Reina", "Shin", "Breana", "Bonny",
    "Susann", "Marjorie", "Loni", "Altha", "Benita", "Ladonna", "Trudie",
    "Madie", "Susie", "Juliana", "Jennette", "Loralee", "Millard", "Linnie",
    "Ward", "Laquanda", "Queenie", "Angelica", "Sima", "Leticia", "Rayford",
    "Shanel", "Emmitt", "Buena", "Raquel", "Rhett", "Shelley", "Shantae",
    "Norbert", "Kristi", "Barbie", "Jeromy", "Ericka", "Giovanna", "Chester",
    "Karly", "Starla", "Rigoberto", "Marti", "Willy", "Marjory", "Julianne",
    "Armando", "Julius", "Lorinda", "Shena", "Ken", "Thomasena", "Joline",
    "Minda", "Sigrid", "Robby", "Erin", "Rachell", "Reid", "Tomas", "Keith",
    "Raina", "Carissa", "Noemi", "Brandon", "Janee", "Dia", "Karen", "Collen",
    "Doria", "Kaye", "Katina", "Micki", "Robin", "Alexis", "Phoebe", "Ione",
    "Delsie", "Reginia", "Daphine", "Ozella", "Ayesha", "Jesus", "Thomasine",
    "Wiley", "Nena", "Krystyna", "Talitha", "Oswaldo", "Violette", "Mollie",
    "Christia", "Irma", "Stephen", "Myrna", "Joann", "Toni", "Lesa", "Isiah",
    "Jackqueline", "Reba", "Dallas", "Annabel", "Blossom", "Regena", "Venus",
    "Sanjuanita", "Les", "Buddy", "Myriam", "Clinton", "Vivien", "Grayce",
    "Caroyln", "Basilia", "Yun", "Branda", "Tambra", "Laila", "Katlyn",
    "Sherise", "Libby", "Lakeesha", "Clotilde", "Hipolito", "Casey", "Jene",
    "Danielle", "Kristian", "Georgie", "Carman", "Aleta", "Heath", "Tiffaney",
    "Samatha", "Danita", "Sari", "Carmelia", "Alda", "Isidro", "Gwenda",
    "Benton", "Mitzi", "Irvin", "Junior", "Valerie", "Tierra", "Carmelita",
    "Mildred", "Yvonne", "Charlena", "Violeta", "Zenobia", "Aletha", "Hana",
    "Moira", "Terese", "Jerica", "Adrianne", "Edward", "Jame", "Lucio", "Tiesha",
    "Raymundo", "Ariel", "Marguerita", "Ernest", "Donnell", "Addie", "Yolande",
    "Marlen", "Su", "Zack", "Tena", "Freeda", "Kenna", "Deetta", "Leta",
    "Jeanna", "Zetta", "Nan", "Brian", "Katheleen", "Bok", "Roy", "Debbi",
    "Hilario", "Deonna", "Athena", "Paola", "Suzy", "Farrah", "Tyler", "Ozie",
    "Ermelinda", "Moon", "Bernard", "Danuta", "Petronila", "Perry", "Annett",
    "Danny", "Gudrun", "Jeannine", "Claud", "Soo", "Merri", "Serita", "Jake",
    "Corrinne", "Zona", "Colin", "Kandra", "Kyla", "Rocky", "Juanita", "Dewayne",
    "Tula", "Catalina", "Justin", "Earle", "Loida", "Ruben", "Wilmer", "Judith",
    "Doreatha", "Cletus", "Sonja", "Yuki", "Augusta", "Tabitha", "Caterina",
    "Lianne", "Beryl", "Shauna", "Juliann", "Daren", "Gaynell", "Renda",
    "Yuonne", "Dennise", "Stephanie", "Danica", "Ammie", "Shanna", "Johnna",
    "Aida", "Marlin", "Libbie", "Kimberley", "Tanika", "Claudio", "Denise",
    "Jay", "Dorthey", "Emile", "Luana", "Vi", "Cherelle", "Alene", "Lavenia",
    "Cristy", "Ruby", "Maximo", "Cristal", "Donald", "Denisha", "Carlton",
    "Leroy", "Vanetta", "Stephane", "Justine", "Augustina", "Michelina", "Karol",
    "Mirtha", "Aleen", "Pandora", "Loyd", "Fae", "Lakeshia", "Michell", "Margo",
    "Shalon", "Arturo", "Tori", "Angeline", "Shawnna", "Natividad", "Clarinda",
    "Maryellen", "Francesca", "Mervin", "Michale", "Elijah", "Minna", "Beverlee",
    "Bettina", "Lora", "Fallon", "Mohammad", "Vito", "Jayson", "Silvia", "Grace",
    "Claudette", "Tari", "Tammy", "Latonya", "Albertina", "Nadia", "Trudy",
    "Dawne", "Charity", "Shizue", "Andrew", "Parker", "Marietta", "Adeline",
    "Ivana", "Ralph", "Nicki", "Krishna", "Jamaal", "Jenise", "Candis",
    "Dorinda", "Cori", "Kimber", "Ronna", "Shizuko", "Kirsten", "Sharie",
    "Howard", "Leola", "Cleo", "Jenna", "Era", "Jacqualine", "Broderick",
    "Dylan", "Bradley", "Karey", "Dewey", "Karie", "Theresia", "Lashon",
    "Joycelyn", "Chun", "Takako", "Rebecca", "Shellie", "Hugo", "Mickie",
    "Lenore", "Magdalena", "Mammie", "Jamison", "Delmar", "Richie", "Belinda",
    "Berry", "Lasandra", "Fredia", "Vina", "Troy", "Sharleen", "Nathalie",
    "Carita", "Sherrell", "Alease", "Hui", "Lilla", "Treva", "Marco", "Khalilah",
    "Ed", "Thalia", "Shaneka", "Darci", "Kathy", "Lowell", "Harriet", "Ardelle",
    "Michal", "Latisha", "Flora", "Jenelle", "Bryce", "Carolina", "Harriette",
    "Barbra", "Carolynn", "Zackary", "Rema", "Coletta", "Madelaine", "Anton",
    "Margart", "Maegan", "Amos", "Ila", "Nery", "Jacqui", "Hope", "Mavis",
    "Candi", "Tomi", "Celinda", "Ben", "Fidel", "Lyle", "Maribeth", "Granville",
    "Sarai", "Brandie", "Chad", "Sindy", "Tawanda", "Maria", "Rosie", "Stewart",
    "Else", "Ezekiel", "Emilia", "Karyl", "Jasmin", "Vickie", "Javier",
    "Deloris", "Ester", "Flor", "Shirley", "Kristel", "Josie", "Mohammed",
    "Fran", "Elsy", "Fiona", "Jenette", "Lurline", "Chet", "Tim", "Leatha",
    "Laronda", "Della", "Gregg", "Rosenda", "Bonita", "Antonietta", "Ashlyn",
    "Conchita", "Jeanette", "Loreta", "Jewell", "Marylou", "Janean", "Onita",
    "Laure", "Lyndia", "Matha", "Lauralee", "Dan", "Delpha", "Basil", "Sarita",
    "Dalene", "Glennie", "Tressa", "Carolann", "Maryalice", "Norberto",
    "Willena", "Shirlene", "Elias", "Alberta", "Clarisa", "Mui", "Dannette",
    "Corazon", "Anika", "Annamaria", "Rebekah", "Isela", "Leopoldo", "Benjamin",
    "Hobert", "Machelle", "Gordon", "Lynette", "Aline", "France", "Lillie",
    "Lizabeth", "Sung", "Lottie", "Anisha", "Malia", "Royal", "Lucinda",
    "Carley", "Hyacinth", "Isaac", "Monet", "Asa", "Sena", "Jo", "Melodi",
    "Evalyn", "Brenton", "Susannah", "Humberto", "Blythe", "Neida", "Lashonda",
    "Pearlene", "Shannon", "Bernadine", "Stasia", "Sau", "Michael", "Ceola",
    "Christinia", "Marylouise", "Millie", "Sunshine", "Theressa", "Nada",
    "Roscoe", "Doug", "Jennefer", "Delena", "Kenyetta", "Kenisha", "Benito",
    "Davis", "Oma", "Jody", "Dalton", "Trudi", "Romona", "Dennis", "Corrie",
    "Marcelo", "Kenton", "Werner", "Collin", "Kaycee", "Alexandra", "Kimberly",
    "Xiao", "Rosalva", "Elayne", "Margie", "Cheree", "Guy", "Shiloh", "Trinh",
    "Sharice", "Katelin", "Ona", "Oliver", "Sanjuana", "Rosy", "Chance", "Elise",
    "Tabatha", "Manual", "Maia", "Lesli", "Vashti", "Boyd", "Steffanie",
    "Sharon", "Luke", "Cami", "Antony", "Kathrine", "Pauline", "Denny",
    "Danille", "Evelynn", "Leesa", "Audry", "Barrie", "Li", "Oralee", "Chara",
    "Doreen", "Mimi", "Vincenza", "Sha", "Loma", "Karoline", "Maryjane", "Tessa",
    "Cathi", "Tamala", "Nicolas", "Annabelle", "Kala", "Janel", "Johana",
    "Imelda", "Lorilee", "Kina", "Herbert", "Berniece", "Tonda", "Rosetta",
    "Jasmine", "Jackelyn", "Edyth", "Adrienne", "Everette", "Hyo", "Jolene",
    "Fredda", "Sharilyn", "Justa", "Abbie", "Shakia", "Johnny", "Dorthy",
    "Corliss", "Eddy", "Toshia", "Francisco", "Eldon", "Shenna", "Nidia",
    "Leonore", "Marisa", "Orval", "Elli", "Renata", "Deandrea", "Zola", "Shaun",
    "Marcia", "Beatris", "Rhona", "Ai", "Dessie", "Kareem", "Gerri", "Otis",
    "Desmond", "Chrystal", "Fausto", "Marguerite", "Neoma", "Benedict", "Alica",
    "Vergie", "Demetria", "Emerald", "Almeda", "Emmy", "Gerda", "Dario", "Risa",
    "Milda", "Kayce", "Lise", "Kristen", "Agripina", "Nora", "Stephine",
    "Marlon", "Anna", "Charmaine", "Chantay", "Margareta", "Serena", "Wilburn",
    "Luanna", "Hilton", "Charis", "Felicia", "Bernita", "Gay", "Erich",
    "Clarine", "Zana", "Marth", "Marisha", "Russel", "Selena", "Treena", "Zonia",
    "Rena", "Carol", "Olen", "Deeann", "Darron", "Jessika", "Emiko", "Lachelle",
    "Latrice", "Darius", "Jenni", "Ahmad", "Rosa", "Jed", "Cara", "Fabian",
    "Kandi", "Maryanna", "Lien", "Florence", "Pamula", "Charisse", "Audria",
    "Georgia", "Gwyn", "Xochitl", "Mora", "Margrett", "Lorita", "Aurea", "Tilda",
    "Dale", "Ruth", "Cory", "Sabina", "Roseann", "Phylicia", "Dominick", "Lia",
    "Lynsey", "Elton", "Sol", "Gretta", "Kristopher", "Tanna", "Latosha",
    "Richelle", "Lon", "Rubye", "Leda", "Fransisca", "Ronnie", "Brinda", "Kym",
    "Sergio", "Kaleigh", "Lieselotte", "Tamisha", "Lani", "Caroll", "Yuko",
    "See", "Jackie", "Celestina", "Teddy", "Pasty", "Donte", "Toccara",
    "Lissette", "Palmira", "Crista", "Randall", "Tai", "Luigi", "Lolita",
    "Jacob", "Dollie", "Dinah", "Quincy", "Brain", "Lenna", "Brynn", "Wanda",
    "Estela", "Hildegard", "Glendora", "Caprice", "Nereida", "Jaymie", "Mikki",
    "Leonida", "Johanne", "Melodee", "Yasuko", "Markita", "Darcie", "Barry",
    "Erma", "Margery", "Brook", "Rosario", "Myron", "Gisela", "Isreal",
    "Kandice", "Kory", "Fawn", "Hung", "Val", "Breanne", "Colton", "Lashunda",
    "Jackson", "Brenna", "Doloris", "Temeka", "Effie", "Kasey", "Juli",
    "Sharonda", "Adrianna", "Kamala", "Grisel", "Rutha", "Sherie", "Angila",
    "Raelene", "Lawana", "Leonarda", "Kati", "Domenic", "Audrea", "Laurie",
    "Sun", "Jona", "Imogene", "Inger", "Wilford", "Eliza", "Hayden", "Burma",
    "Judi", "Robena", "Bobette", "Yanira", "Nieves", "Stanford", "Ashlee",
    "Saran", "Milissa", "Cherri", "Tamiko", "Marlo", "Ashely", "Faviola",
    "Micheline", "Nilda", "Jenell", "Youlanda", "Vicky", "Tequila", "Cindi",
    "Archie", "Ramona", "Brigitte", "Rosemary", "Breanna", "Lynn", "Kaley",
    "Melida", "Shelia", "Monique", "Emerita", "Margarette", "Germaine",
    "Helaine", "Marylee", "Georgiann", "Dalia", "Mellissa", "Dimple", "Shirely",
    "Leann", "Raven", "Inocencia", "Loan", "Avery", "Wilton", "Arlette",
    "Catrice", "Tobi", "Darlene", "Nakisha", "Ettie", "Yer", "Alejandrina",
    "Wilfred", "Madalyn", "Alonso", "Mardell", "Joetta", "Owen", "Emeline",
    "Nelly", "Shenika", "Nella", "Jacelyn", "Tamesha", "Teresa", "Jolynn",
    "Vance", "Lynelle", "Zada", "Mercedes", "Marci", "Blanche", "Birdie", "Jong",
    "Iliana", "Mercy", "Elicia", "Viviana", "Marcelina", "Jonnie", "Gabrielle",
    "Liliana", "Nanette", "Marry", "Wes", "Joyce", "Verline", "Delmy", "Kathlyn",
    "Salena", "Joey", "Senaida", "Coy", "Carole", "Marisol", "Cyril", "Alonzo",
    "Eduardo", "Olivia", "Jen", "Elina", "Lenny", "Golden", "Lavonna", "Amy",
    "Phuong", "Emily", "Rene", "Tawana", "Nettie", "Sharri", "Hortensia", "Gwen",
    "Alfred", "Calandra", "Lara", "Moshe", "Creola", "Rodger", "Aretha", "Mari",
    "Thu", "Karren", "Marybelle", "Pearlie", "Candra", "Robyn", "Shemika",
    "Lillia", "Bernarda", "Katrice", "Donn", "Palma", "Sade", "Rosendo",
    "Pearly", "April", "Evonne", "Donnette", "Minh", "Deane", "So", "Dean",
    "Manie", "Miki", "Josue", "Kurt", "Gustavo", "Robert", "August", "Jamal",
    "Lyla", "Dino", "Adalberto", "Hilda", "Carolee", "Tinisha", "Ramiro",
    "Bonnie", "Alline", "Marcella", "Arielle", "Laurine", "Jenniffer", "Brianna",
    "Grover", "Angelika", "Meredith", "Elana", "Cathryn", "Jeff", "Elene",
    "Venessa", "Willow", "Lincoln", "Jaunita", "Cyndy", "Patsy", "Maile",
    "Kendall", "Rona", "Ida", "Silas", "Lala", "Un", "Marlana", "Neta", "Arlyne",
    "Lynnette", "Donnie", "Emelia", "Marylynn", "Rosalinda", "Keven", "Joan",
    "Iris", "Sheron", "Consuelo", "Beula", "Meg", "Carletta", "Afton",
    "Maryanne", "Sarina", "Otilia", "Noe", "An", "Ardith", "Pamila", "Stefany",
    "Althea", "Alvera", "Dorotha", "Karri", "Terrence", "Marilee", "Teodora",
    "Jacklyn", "Trish", "Melany", "Bill", "Maritza", "Tameka", "Cecille", "Jone",
    "Lena", "Tobie", "Rosena", "Gussie", "Tresa", "Aileen", "Zula", "Janna",
    "Phebe", "Lawerence", "Amelia", "Rosette", "Wilhelmina", "Constance", "Sana",
    "Corene", "Jammie", "Walker", "Earnestine", "Edmundo", "Ka", "Akiko",
    "Trena", "Elwanda", "Verona", "Sabra", "Chante", "Naoma", "Lydia", "Natacha",
    "Harriett", "Janeth", "Chantelle", "Alfredia", "Brittny", "Dayle", "Curt",
    "Brock", "Pat", "Leanora", "Wilda", "Drew", "Brendon", "Kasi", "Arlena",
    "Louella", "Orpha", "Rosalee", "Reiko", "Randell", "Floyd", "Natosha",
    "Sherlyn", "Blanch", "Eldridge", "Bebe", "Nicholle", "Terra", "Antonette",
    "Emmaline", "Maisha", "Ellan", "Dane", "Lakiesha", "Rosia", "Lisha",
    "Nadine", "Calvin", "Ashli", "Kizzy", "Serafina", "Jeanetta", "Caren",
    "Laquita", "Enda", "Migdalia", "Zulema", "Joshua", "Becki", "Magan", "Shon",
    "Zoraida", "Marshall", "Loretta", "Mario", "Daniele", "Jon", "Caitlin",
    "Tess", "Maisie", "Ena", "Kacy", "Hanh", "Tory", "Priscila", "Leone",
    "Roberto", "Savanna", "Adelina", "Ria", "Joleen", "Andy", "Kylee", "Lan",
    "Lola", "Janetta", "Selene", "Gracie", "Jaclyn", "Cruz", "Erna", "Teresita",
    "Tyrell", "Lucienne", "Rana", "German", "Lura", "Helene", "Adina", "Lin",
    "Janett", "Peter", "Crissy", "Zachery", "Princess", "Laurette", "Hortense",
    "Marvis", "Andera", "Mirella", "Jazmin", "Randal", "Damon", "Enola", "Roxie",
    "Antwan", "Anjanette", "Vernita", "Kelsi", "Willia", "Wenona", "Marlene",
    "Divina", "Micaela", "Fidela", "Brianne", "Joselyn", "Buford", "Daryl",
    "Arlinda", "Alix", "Kaitlin", "Roselyn", "Kisha", "Candy", "Kacie", "Daisy",
    "Gita", "Lilli", "Lester", "Ilona", "Winston", "Alesia", "Chris", "Malena",
    "Tom", "Hwa", "Porsha", "Twila", "Deanna", "Errol", "Carlota", "Elease",
    "Meghann", "Shawanda", "Cherie", "Ellsworth", "Sharyl", "Rosalba",
    "Ramonita", "Sharan", "Alycia", "Antonia", "Loreen", "Jerrell", "Merrilee",
    "Rachele", "Alaine", "Rudy", "Chanell", "Sebastian", "Brenda", "Amanda",
    "Jesenia", "Kami", "Clare", "Merilyn", "Gavin", "Leanna", "Allena",
    "Modesto", "Maximina", "Abdul", "Neely", "Gia", "Torri", "Bernie", "Valarie",
    "James", "Tarah", "Elmo", "Earlie", "Maxine", "Richard", "Holli", "Oretha",
    "Vernetta", "Leida", "Fatima", "Asley", "Nona", "Hyon", "Sofia", "Agueda",
    "Venetta", "Giuseppe", "Candie", "Colby", "Rima", "Mandi", "Emory",
    "Francesco", "Herb", "Lasonya", "Kathlene", "Carleen", "Thresa", "Bea",
    "Theda", "Brandy", "Tonette", "Fleta", "Khadijah", "Jarvis", "Tish",
    "Sophia", "Liz", "Teofila", "Logan", "Dorie", "Anderson", "Mandie", "Elida",
    "Angelyn", "Gloria", "Kip", "Pamella", "Bertha", "Sparkle", "Lino", "Jonas",
    "Pura", "Wai", "Christopher", "Lupe", "Lavinia", "Jonell", "Charley",
    "Sonny", "Rubie", "Jamila", "Kaitlyn", "Tien", "Kristal", "Sara", "Heather",
    "Antione", "Garland", "Ignacia", "Isidra", "Debora", "Mitchel", "Evelin",
    "Shanae", "Gena", "Sid", "Keshia", "Joi", "Lois", "Santo", "Miguelina",
    "Georgianna", "Neal", "Flavia", "Carmelo", "Maude", "Leslee", "Gerardo",
    "Charita", "Dana", "Olin", "Karolyn", "Colette", "Lindsy", "Mendy", "Vicki",
    "Isabella", "Rhea", "Maris", "Maren", "Celia", "Carlena", "Vernice",
    "Ashley", "Tangela", "Ute", "Jaimie", "Leighann", "Quinn", "Stuart", "Tonja",
    "Fern", "Darwin", "Vinnie", "Alane", "Aubrey", "Providencia", "Bette",
    "Patti", "Tonya", "Ashton", "Greg", "Prince", "Shavonne", "Gina", "Adriane",
    "Esmeralda", "Billy", "Rebbeca", "Elvina", "Elfriede", "Aron", "Samuel",
    "Booker", "Veronica", "Glenn", "Shaunta", "Santiago", "Roseline", "Marilu",
    "Barbar", "Merideth", "Sallie", "Jenny", "Tana", "Celeste", "Margarite",
    "Gwendolyn", "Abram", "Katy", "Edelmira", "Cuc", "Vanessa", "Allen",
    "Rafaela", "Carmelina", "Noelle", "Soila", "Harvey", "Liane", "Jacqueline",
    "Jina", "Kecia", "Hannah", "Waneta", "Rea", "Enrique", "Teri", "Kittie",
    "Bambi", "Dodie", "Pilar", "Linh", "Julio", "Vanna", "Franchesca", "Lael",
    "Marcela", "Louvenia", "Taunya", "Micheal", "Nancy", "Linwood", "Merlene",
    "Mika", "Earleen", "Eryn", "Rosanna", "Ervin", "Maragaret", "Florance",
    "Delinda", "Shona", "Viola", "Marquetta", "Ursula", "Tomasa", "Hermine",
    "Florida", "Carmella", "Shoshana", "Maryam", "Marty", "Adan", "Alita",
    "Concha", "Jesse", "Nita", "Piedad", "Lyda", "Yoshie", "Renate", "Len",
    "Lorri", "Marni", "Blake", "Jarod", "Natasha", "Gerry", "Sheldon", "Jaime",
    "Fay", "Crysta", "Ethan", "Christina", "Russell", "Phillis", "Jaye",
    "Pearle", "Laurene", "Ernie", "Shyla", "Lorrine", "Thad", "Weston",
    "Emanuel", "Ivonne", "Alphonse", "Doyle", "Clarita", "Gladys", "Avril",
    "Marnie", "Desiree", "Melba", "Kathern", "Efrain", "Teisha", "Luz", "Muriel",
    "Hildred", "Erasmo", "Lauryn", "Lourie", "Matilde", "Ava", "Aimee", "Jayme",
    "Elizebeth", "Kum", "Abe", "Gertha", "Zane", "Kari", "Hulda", "Lisa", "Dann",
    "Rivka", "Jermaine", "Anastacia", "Ngoc", "Stephani", "Hannelore",
    "Francine", "Maggie", "Mariela", "Silvana", "Shenita", "Deedee", "Pearline",
    "Lindsey", "Rosaline", "Marilyn", "Merlin", "Denese", "Beau", "Twanna",
    "Ayana", "Coleman", "Lady", "Mercedez", "Earline", "Krystal", "Suzie",
    "Waltraud", "Veronique", "Regina", "Hong", "Avelina", "Johnathon", "Mariam",
    "Elouise", "Bobbi", "Leo", "Madeline", "Kasandra", "Argelia", "King",
    "Azalee", "Ping", "Isaiah", "Eleonora", "Tawny", "Winter", "Gidget",
    "Nelida", "Eugene", "Marina", "Cayla", "Mitch", "Tona", "Lanette", "Karisa",
    "Le", "Martina", "Karyn", "Ok", "Christian", "Ruthe", "Katerine", "Jettie",
    "Rosella", "Vasiliki", "Love", "Rossie", "Jeraldine", "Son", "Dulcie",
    "Louann", "Denisse", "Minerva", "Olive", "Bettye", "Sharmaine", "Yahaira",
    "Hye", "Allyn", "Melonie", "Benny", "Joanna", "Jamar", "Christoper", "Misti",
    "Taylor", "Joe", "Betsey", "Holly", "Gennie", "Fermin", "Milagro", "Miriam",
    "Kelli", "Cordell", "Ruthanne", "Sibyl", "Lorriane", "Kassie", "Seema",
    "Chanel", "Johnette", "Carie", "Rodrigo", "Morris", "Sally", "Patria",
    "Otto", "Eulalia", "Liberty", "Marlena", "Tenesha", "Rogelio", "Ozell",
    "Earnest", "Ellen", "Pamela", "Mazie", "Sabrina", "Patrice", "Warner",
    "Cristen", "Chia", "Heriberto", "Melda", "Julia", "Lemuel", "Quinton",
    "Mauro", "Beverly", "Jessi", "Coleen", "Thomas", "Kennith", "Alecia",
    "Keenan", "Oda", "Brigette", "Tonita", "Zenaida", "Leandra", "Ora",
    "Fermina", "Chelsey", "Jimmy", "Dwight", "Latoya", "Cortez", "Casimira",
    "Kenyatta", "Lissa", "Fredericka", "Louis", "Amina", "Maxie", "Quiana",
    "Santos", "Randee", "Norene", "Tiffanie", "Linnea", "Fredrick", "Amie",
    "Angelic", "Tanesha", "Lorna", "Onie", "Rufus", "Elroy", "Antoine", "Suanne",
    "Madeleine", "Kesha", "Alva", "Cassie", "Luanne", "Terrell", "Cicely",
    "Karissa", "Tarra", "Seymour", "Florrie", "Sherry", "Keila", "Israel",
    "Erminia", "Tillie", "Lucile", "Terrance", "Cris", "Cole", "Adaline", "Man",
    "Vicente", "Reena", "Lavina", "Julie", "Tristan", "Phung", "Vivian", "Fred",
    "Heike", "Ewa", "Gaylord", "Marisela", "Joesph", "Mallory", "Anamaria",
    "Rickey", "Genesis", "Claudie", "Eleanor", "Quentin", "Melisa", "Carmina",
    "Paul", "Shea", "Ariane", "Delmer", "Esperanza", "Marielle", "Leandro",
    "Voncile", "Scot", "Tiffiny", "Leilani", "Catherina", "Kacey", "Cassondra",
    "Kyung", "Juliane", "Wilber", "Anastasia", "Willie", "Danelle", "Inga",
    "Anisa", "Steve", "Christen", "Carola", "Lavon", "Rubin", "Jack", "Mitzie",
    "Delana", "Lita", "Heidy", "Josh", "Tia", "Micah", "Burt", "Catherine",
    "Sharda", "Ailene", "Ike", "Edie", "Keisha", "Darrin", "Salina", "Delila",
    "Caryl", "Lore", "Floy", "Cary", "Sharron", "Newton", "Valentina", "Eusebio",
    "Lionel", "Stacie", "Tonia", "Lavada", "Marcus", "Lona", "Elenor",
    "Catherin", "Shanda", "Wade", "Louetta", "Estella", "Mattie", "Rosann",
    "Hermila", "Vaughn", "Gene", "Somer", "Marine", "Lashaun", "Randa", "Renae",
    "Tam", "Derick", "Shanell", "Tamekia", "Kimiko", "Norah", "Georgene",
    "Vincent", "Livia", "Remona", "Latonia", "Zella", "Marva", "Waylon",
    "Kathyrn", "Brett", "Giselle", "Jeri", "Lanora", "Raymon", "Wilbur", "Retta",
    "Diedra", "Jospeh", "Sherley", "Sanda", "Dusti", "Petrina", "Randy",
    "Shakita", "Reda", "Vernia", "Cecil", "Lucina", "Elinor", "Houston", "Patty",
    "Kendal", "Astrid", "Hoa", "Laurena", "Ilana", "Beth", "Cythia", "Akilah",
    "Elisha", "Fabiola", "Salvatore", "Judson", "Elly", "Babette", "Marla",
    "Edgardo", "Alesha", "Carmon", "Monica", "Yuriko", "Tanja", "Katheryn",
    "Noreen", "Jessie", "Genie", "Melody", "Barton", "Tatiana", "Geraldo",
    "Dawn", "Ardath", "Corrin", "Shawnda", "Jami", "Lisabeth", "Maye", "Graig",
    "Shandra", "Teressa", "Christiana", "Janet", "Lazaro", "Estelle", "Valene",
    "Ricky", "Scott", "Tyson", "Anh", "Gerard", "Trang", "Solomon", "Obdulia",
    "Caridad", "Mauricio", "Breann", "Darla", "Nicolasa", "Margaretta", "Shelly",
    "Winifred", "Mee", "Carrie", "Margarito", "Maya", "Tashina", "Tomoko",
    "Mathilde", "Omega", "Pei", "Elena", "Freddy", "Maudie", "Gianna", "Mariah",
    "Tayna", "Lovie", "Roland", "Remedios", "Hee", "Columbus", "Lupita",
    "Jerold", "Dierdre", "Glynda", "Verlie", "Domenica", "Yen", "Kary", "Joy",
    "Shayla", "Wilfredo", "Josephine", "Theo", "Apryl", "Lindy", "Concetta",
    "Rolanda", "Reinaldo", "Sylvia", "Dagny", "Anitra", "Leigh", "Alphonso",
    "Joella", "Gretchen", "Jannie", "Kourtney", "Eusebia", "Yuette", "Lane",
    "Mose", "Buffy", "Yasmin", "Jesica", "Hermina", "Rebbecca", "Jacquelyne",
    "Racquel", "Nathaniel", "Parthenia", "Maybelle", "Felecia", "Kasie",
    "Suzanne", "Chelsie", "Evelyne", "Niki", "Darlena", "Fe", "Paris", "Darby",
    "Tomeka", "Twyla", "Dave", "Carter", "Regenia", "Joni", "Mina", "Alejandra",
    "Marcel", "Shani", "Catina", "Graciela", "Darell", "Hermelinda", "Alayna",
    "Misty", "Staci", "Lettie", "Angelique", "Elinore", "Rosalina", "Eladia",
    "Jason", "Nanci", "Gertude", "Noma", "Marceline", "Stephnie", "Tasia",
    "Blondell", "Adelaide", "Victoria", "Marna", "Mignon", "Alpha", "Lisbeth",
    "Nolan", "Celesta", "Tasha", "Cassi", "Tobias", "Ellie", "Valeri", "Neomi",
    "Syble", "Patrick", "Dulce", "Davida", "Theola", "Normand", "Devon",
    "Mireille", "Ha", "Raleigh", "Emmett", "Harry", "Miles", "Masako", "Shasta",
    "Evelia", "Rhonda", "Darrick", "Lyman", "Augustus", "Lucilla", "Mariette",
    "Sharita", "Hilde", "Alleen", "Lang", "Romaine", "Cherlyn", "Latesha",
    "Kerri", "Santa", "Gilbert", "Sacha", "Ofelia", "Lina", "Jayne", "Karin",
    "Daniella", "Peggy", "Dominic", "Suzi", "Jennie", "Mel", "Edwin",
    "Altagracia", "Latarsha", "Danna", "Annabell", "Ned", "Setsuko", "Janelle",
    "Shanice", "Angeles", "Leia", "Junko", "Britt", "Marica", "Gertrude",
    "Ginny", "Nikia", "Katelyn", "Jolie", "Nova", "Chere", "Luvenia", "Merle",
    "Victorina", "Glennis", "Regine", "Argentina", "Andreas", "Fumiko",
    "Frederic", "Shaunte", "Kermit", "Elois", "Emelina", "Garfield", "Boris",
    "Tanisha", "Kevin", "Foster", "Ariana", "Ngan", "Zoe", "Winnie", "Roxanne",
    "Elia", "Kristyn", "Cleotilde", "Huey", "Luise", "Elizabet", "Eliseo",
    "Jena", "Ranae", "Pinkie", "Krista", "Ronny", "Melanie", "Jewel", "Lashay",
    "Ricardo", "Palmer", "Sonya", "Paulette", "Margarett", "Deirdre", "Long",
    "Tamra", "Davina", "Lana", "Calista", "Iluminada", "Hailey", "Marsha",
    "Isabelle", "Aleisha", "Tammi", "Renee", "Brent", "Felisha", "Allyson",
    "Elda", "Usha", "Charmain", "Pablo", "Hassie", "Paula", "Sang", "Clifford",
    "Savannah", "Hedy", "Todd", "Clement", "Cindy", "Kelvin", "Mellie", "Bess",
    "Reginald", "Laurinda", "Stephaine", "Hilma", "Katelynn", "Jessica", "Ma",
    "Gabriel", "Landon", "Yelena", "Laureen", "Mariann", "Josiah", "Karma",
    "Jacque", "Dayna", "Trenton", "Charissa", "Jamika", "Dianna", "Jolanda",
    "Chasity", "Haywood", "Carson", "Jordan", "Mozelle", "Nathanial", "Dorethea",
    "Monika", "Lynwood", "Charlyn", "Paulene", "Alvaro", "Willetta", "Pasquale",
    "Collene", "Corrine", "Elodia", "Jenae", "Lennie", "Rachal", "Myrl",
    "Moises", "Bruna", "Jocelyn", "Helen", "Soon", "Jennell", "Vada", "Rodolfo",
    "Daysi", "Alana", "Mafalda", "Flo", "Grady", "Bruce", "Lean", "Siu",
    "Adella", "Rosamond", "Matt", "Arden", "Tracee", "Rozanne", "Dagmar",
    "Sheena", "Don", "Bernadette", "Theodore", "Byron", "Kerstin", "Shery",
    "Forest", "Jacqulyn", "Ardelia", "Stefanie", "Yee", "Francis", "Janiece",
    "Herschel", "Shawnta", "Marissa", "Emelda", "Cristina", "Mariana", "Rupert",
    "Tama", "Bryan", "Tonisha", "Fonda", "Belkis", "Georgeanna", "Louie",
    "Mittie", "Milan", "Helga", "Lakita", "Brittni", "Frankie", "Gonzalo",
    "Magali", "Cammy", "Eloy", "Zofia", "Lonna", "Guillermina", "Margret",
    "Belen", "Julee", "Ryann", "Pedro", "Chanda", "Deidra", "Nola", "Denita",
    "Florencio", "Jennine", "Anita", "Elane", "Annie", "Kyoko", "Antonina",
    "Zachary", "Merissa", "Shirl", "Rudolph", "Francina", "Marcy", "Jimmie",
    "Isadora", "Krysten", "Cecily", "Nichelle", "Kirstie", "Eustolia",
    "Marybeth", "Sandee", "Shavon", "Latasha", "Hisako", "Dung", "Shonna",
    "Soledad", "Jefferey", "Camelia", "Julianna", "Charleen", "Aja", "Elsie",
    "Dominque", "Truman", "Domonique", "Renato", "Keely", "Shavonda",
    "Kimberlee", "Modesta", "Oneida", "Theodora", "Brady", "Adelle", "Lavera",
    "Mariella", "Ferdinand", "Laurel", "Elvie", "Leeanna", "Hae", "Angel",
    "Johnie", "Lucille", "Corinne", "Verlene", "Ashly", "Fannie", "Catharine",
    "Drema", "Erinn", "Janita", "Vernie", "Leonor", "Terisa", "Tawna", "Berna",
    "Lenora", "Angele", "Zina", "Carlita", "Sebrina", "Vinita", "Kathe",
    "Olevia", "Yukiko", "Marie", "Maureen", "Myles", "Irena", "Vern", "Dorothy",
    "Alfonso", "Stanley", "Stevie", "Morton", "Kendrick", "Ela", "Millicent",
    "Enedina", "Mistie", "Amparo", "Rosita", "Miguel", "Josef", "Jacquelynn",
    "Felipa", "Tyron", "Dion", "Hal", "Kathleen", "Jolyn", "Yuk", "Velvet",
    "Terri", "Margy", "Thuy", "Adell", "Jerrica", "Norman", "Alisha", "Julieta",
    "Kelsie", "Roger", "Alta", "Nelle", "Lauren", "Shalanda", "Hoyt", "Desirae",
    "Ling", "Rodney", "Evangeline", "Shawana", "Emma", "David", "Damien",
    "Cristobal", "Nancey", "Meridith", "Rueben", "Heide", "Charline", "Eugena",
    "Delbert", "Lisandra", "Sueann", "Melissia", "Crystal", "Gregoria", "Dahlia",
    "Arline", "May", "Rosio", "Carlene", "Catarina", "Georgann", "Hassan",
    "Reanna", "Shonda", "Douglass", "Dong", "Emery", "Wonda", "Woodrow",
    "Patrina", "Gabriele", "Tifany", "Malvina", "Shira", "Evia", "Luella",
    "Tuan", "Santina", "Elizabeth", "Ruthie", "Shelby", "September", "Alissa",
    "Harland", "Kali", "Claudine", "Delia", "Jesusita", "Sherron", "Lorraine",
    "Geoffrey", "Leon", "Brittany", "Devin", "Heidi", "Irene", "Xuan",
    "Drusilla", "Janessa", "Evan", "Deandre", "Sirena", "Wynell", "Sheryll",
    "Lily", "Cinda", "Tawnya", "Chas", "Noriko", "Alyse", "Agatha", "Jude",
    "Nilsa", "Kara", "Nannie", "Cassandra", "Angelina", "Scarlett", "Norma",
    "Darin", "Tashia", "Odell", "In", "Dena", "Keneth", "Earl", "Rashida",
    "Kirk", "Dorathy", "Roma", "Pam", "Goldie", "Melissa", "Nu", "Felton",
    "Tamika", "Kori", "Toshiko", "Tenisha", "Berta", "Maryrose", "Exie", "Lu",
    "Trent", "Carolyne", "Alba", "Shantell", "Brigida", "Luis", "Vernon",
    "Denis", "Rolland", "Terrilyn", "Edison", "Ebonie", "Maryann", "Carina",
    "Edwina", "Wen", "Chantel", "Cathey", "Carly", "Roseanna", "Efren", "Dalila",
    "Lilly", "Keri", "Waldo", "Eartha", "Minta", "Thersa", "Amada", "Santana",
    "Candelaria", "Venita", "Candice", "Sierra", "Sean", "Verla", "Phillip",
    "Deja", "Alena", "Micha", "Mira", "Karole", "Sandy", "Jalisa", "Sheba",
    "Iona", "Chastity", "Shila", "Verdell", "Keeley", "Song", "Annita", "Irving",
    "Nikki", "Ivey", "Magdalene", "Rory", "Marc", "Lorine", "Bertie", "Roderick",
    "Cherry", "Jacques", "Chau", "Meghan", "Edmund", "Essie", "Jonie", "Jeffie",
    "Maricela", "Lashawnda", "Winford", "Idalia", "Virgil", "Arletta", "Loree",
    "Kera", "Damion", "Elissa", "Candyce", "Jillian", "Nell", "Lilian",
    "Coralie", "Noel", "Delaine", "Carolin", "Tatum", "Maragret", "Kenya",
    "Hanna", "Tyesha", "Arnold", "Eunice", "Margit", "Glayds", "Lorenza",
    "Edgar", "Wilhemina", "Faustino", "Samual", "Yung", "Larissa", "Beulah",
    "Ulysses", "Lacresha", "Armandina", "Coretta", "Suzette", "Ann", "Ada",
    "Margeret", "Kristina", "Kaylene", "Star", "Enid", "Ivelisse", "Florentina",
    "Bessie", "Lorelei", "Dominica", "Delta", "Manuel", "Viva", "Elliott",
    "Ethelyn", "Willette", "Kelle", "Leora", "Nila", "Lovella", "Sandi",
    "Lavelle", "Lea", "Cyndi", "Ivette", "Marlys", "Isabel", "Stefania",
    "Ayanna", "Beckie", "Thurman", "Kylie", "Alysha", "Ilda", "Gerald", "Zora",
    "Lori", "Mara", "Ramon", "Bud", "Mahalia", "Brittaney", "Zena", "Rob",
    "Charlette", "Meryl", "Nicol", "Eddie", "Alexander", "Matthew", "Frieda",
    "Demetrius", "Leona", "Albina", "Abby", "Diedre", "Vertie", "Vera", "Bulah",
    "Chantal", "Bell", "Fairy", "Shana", "Melvina", "Susana", "Shalonda",
    "Eilene", "Bethanie", "Mertie", "Alfredo", "Bernardina", "Paulita",
    "Yolonda", "Annis", "Markus", "Noella", "Rich", "Sondra", "Kamilah",
    "Celina", "Casandra", "Dorothea", "Dorian", "Shamika", "Danae", "Juan",
    "Major", "Erlene", "Ethel", "Alicia", "Jonelle", "Meda", "Jeanie", "Cora",
    "Yi", "Nia", "Chaya", "Sherilyn", "Raphael", "Stefani", "Delora", "Alfonzo",
    "Ta", "Judie", "Dannielle", "Cleveland", "Bret", "Aleshia", "Lilliana",
    "Miyoko", "Victor", "Maud", "Ilse", "Ellamae", "Delphine", "Kristan",
    "Preston", "Ara", "Loyce", "Freida", "Pamala", "Deshawn", "Hollis", "Elmira",
    "Vena", "Kanisha", "Chassidy", "Melodie", "Roselle", "Vita", "Angelena",
    "Young", "Page", "Billye", "Jeanett", "Margorie", "Theron", "Gale", "Janie",
    "January", "Gema", "Blanca", "Christin", "Yang", "Lucretia", "Kim", "Jose",
    "Ima", "Sharla", "Eleni", "Shaquita", "Galen", "Jae", "Shaunda", "Angle",
    "Gaynelle", "Jerlene", "Tamica", "Kiesha", "Tempie", "Eva", "Edmond",
    "Fernande", "Clayton", "Sherly", "Rey", "Daniela", "Ashleigh", "Lorretta",
    "Evie", "Aura", "Sidney", "Belia", "Violet", "Simona", "Cinthia", "Polly",
    "Shanita", "Maple", "Cristopher", "Aldo", "Kayleigh", "Ardis", "Lynda",
    "Allene", "Herminia", "Rosalind", "Dina", "Florine", "Fernanda", "Skye",
    "Ardella", "Jordon", "Kristofer", "Shayne", "Florentino", "Rubi", "Willis",
    "Eugenia", "Alethia", "Kareen", "Bettie", "Shela", "Sherill", "Autumn",
    "Lee", "Xenia", "Tonie", "Donnetta", "Aracelis", "Mack", "Vanda", "Clarice",
    "Karmen", "Debrah", "Bev", "Sommer", "Marcellus", "Jacinda", "Antonetta",
    "Jackeline", "Tosha", "Dyan", "Zulma", "Corrina", "Tatyana", "Lulu",
    "Janell", "Malcolm", "Audie", "Saturnina", "Nellie", "Kenneth", "Marget",
    "Georgetta", "Cinderella", "Mckenzie", "Asuncion", "Sherwood", "Annalee",
    "Colene", "Maynard", "Bobbye", "Raye", "Kazuko", "Deangelo", "Vannesa",
    "Scarlet", "Eden", "Megan", "Latanya", "Tad", "Lexie", "Margarita", "Buck",
    "Etta", "Ciara", "Adolph", "Shari", "Beaulah", "Marylyn", "Amira", "Milly",
    "Aurora", "Danika", "Johanna", "Jacalyn", "Nannette", "Jeanmarie", "Leonel",
    "Mackenzie", "Windy", "Elyse", "Conrad", "Eleanora", "Abraham", "Allison",
    "Lahoma", "Elanor", "Samara", "Alison", "Sammy", "Nichol", "Dione", "Freda",
    "Caron", "Amal", "Fritz", "Laree", "Loren", "Nichole", "Sanora", "Florene",
    "Juliet", "Annmarie", "Arcelia", "Luisa", "Ty", "Barrett", "America",
    "Jacinto", "Garry", "Anneliese", "Candace", "Joannie", "Lizeth", "Maire",
    "Eve", "Cherly", "Jodi", "Madalene", "Caroline", "Virgina", "Ronald",
    "Ethyl", "Cherilyn", "Terina", "Tamatha", "Ferne", "Suzanna", "Tianna",
    "Anglea", "Emilie", "Jeannie", "Sal", "Jerry", "Bobbie", "Robbyn", "Mallie",
    "Chung", "Lynna", "Dede", "Eboni", "Walton", "Ronda", "Toney", "Lucy",
    "Lidia", "Larhonda", "Natalie", "Garnet", "Philip", "Samantha", "Luciana",
    "Genevieve", "Emilee", "Jannette", "Bettyann", "Mao", "Elbert", "Chadwick",
    "Devora", "Brandee", "Angelo", "Jeanene", "Ron", "Charlsie", "Raymond",
    "Tamie", "Yadira", "Cleta", "Jamie", "Erik", "Yvette", "Alia", "Gigi",
    "Cristi", "Corine", "Antone", "Gino", "Liza", "Reyes", "Danyel", "Lory",
    "Pia", "Delcie", "Sharlene", "Gilda", "Dell", "Bruno", "Jess", "Celine",
    "Mireya", "Josphine", "Taina", "Elva", "Dolly", "Margert", "Hien", "Rheba",
    "Aurelia", "Sharell", "Kelly", "Takisha", "Arla", "Kizzie", "Miquel",
    "Therese", "Nakia", "Jonathon", "Alverta", "Lou", "Digna", "Artie",
    "Loriann", "Nana", "Haydee", "Bree", "Torrie", "Dori", "Sherita", "Earlene",
    "Piper", "Tijuana", "Karon", "Selma", "Bronwyn", "India", "Henriette",
    "Kaila", "Thomasina", "Trinidad", "Joanie", "Laurence", "Florencia", "Wyatt",
    "Shakira", "Tennille", "Annamarie", "Syreeta", "Soraya", "Ludie", "Marleen",
    "Prudence", "Ilene", "Leeanne", "Katharina", "Bethel", "Romana", "Eugenio",
    "Tracy", "Marg", "Lacey", "Halina", "Hertha", "Yevette", "Min", "Lesley",
    "Shantay", "Alexia", "Erica", "Cyrstal", "Austin", "Lizette", "Rachelle",
    "Quintin", "Apolonia", "Monroe", "Cammie", "Monnie", "Lanell", "Jarrod",
    "Lavonda", "Gwenn", "Pennie", "Irina", "Georgette", "Na", "Tarsha",
    "Kassandra", "Diego", "Bryanna", "Riley", "Clifton", "Corinna", "Inez",
    "Chi", "Melynda", "Queen", "Ellyn", "Stephania", "Petra", "Celestine",
    "Fanny", "Jeana", "Kenda", "Jessia", "Brande", "Doretha", "Bong", "Timothy",
    "Pete", "Rosalia", "Odelia", "Shae", "Esteban", "Monte", "Ira", "Sherri",
    "Garret", "Stormy", "Illa", "Christal", "Sherlene", "Kiley", "Cordelia",
    "Scottie", "Melania", "Temple", "Codi", "Chelsea", "Nickie", "Velia",
    "Jaleesa", "Sachiko", "Dorthea", "Clay", "Vincenzo", "Odette", "Allie",
    "Omar", "Claire", "Jin", "Buster", "Kathryn", "Bridget", "Eleonore",
    "Francie", "Faith", "Salome", "Sheila", "Maribel", "Rosana", "Miss",
    "Marquis", "Sherman", "Elke", "Mathilda", "Del", "Agnus", "Angie", "Tressie",
    "Jeanine", "Dustin", "Cari", "Tuyet", "Teodoro", "Tommie", "Starr", "Herman",
    "Herma", "Darleen", "Emmanuel", "Douglas", "Letty", "Zita", "Kiyoko", "Yan",
    "Sulema", "Mona", "Seth", "Cedric", "Sharyn", "Betty", "Asha", "Joie",
    "Karine", "Nathanael", "Gilberte", "Magda", "Cindie", "Amberly", "Nerissa",
    "Dorris", "Valentine", "Darrell", "Terica", "Lecia", "Camie", "Mica",
    "Veola", "Oleta", "Raymonde", "Catrina", "Lanita", "Kiana", "Shawna",
    "Manuela", "Lorrie", "Darcy", "Pok", "Sudie", "Wava", "Ricki", "Latrisha",
    "Reatha", "Cecile", "Theresa", "Susy", "Joana", "Darrel", "Wesley", "Simone",
    "Alaina", "Dante", "My", "Terresa", "Dianne", "Jim", "Meggan", "Nakesha",
    "Janene", "Jaquelyn", "Cynthia", "Twana", "Ulrike", "Lashaunda", "Amber",
    "Wynona", "Lajuana", "Mindy", "Joaquina", "Hans", "Joslyn", "Randolph",
    "Lavern", "Kristle", "Shemeka", "Almeta", "Cheyenne", "Taren", "Felice",
    "Arvilla", "Leeann", "Evette", "Yon", "Camellia", "Clemente", "Julian",
    "Tami", "Jamey", "Joane", "Ghislaine", "Joaquin", "Georgina", "Jovan",
    "Jenine", "Janette", "Bennett", "Kortney", "Gaston", "Madelyn", "Rochell",
    "Tammara", "Hedwig", "Nick", "Ambrose", "Branden", "Joya", "Claribel",
    "Beata", "Loris", "Chasidy", "Donetta", "Kellye", "Marcos", "Tesha",
    "Ingrid", "Kieth", "Naomi", "Maryland", "Lorette", "Cedrick", "Bernice",
    "Twanda", "Cheryll", "Charlene", "Larita", "Merlyn", "Carl", "Kyong",
    "Marinda", "Frederick", "Julietta", "Marilou", "Levi", "Bibi", "Gladis",
    "Magen", "Xiomara", "Daniel", "Rozella", "Alyson", "Traci", "Arianne",
    "Candida", "Kerry", "Shaniqua", "Donovan", "Doris", "Tyisha", "Adah",
    "Fernando", "Trula", "Ciera", "Leland", "Madge", "Danette", "Talisha",
    "Tommy", "Perla", "Jetta", "Chae", "Joelle", "Latoyia", "Garth", "Virgilio",
    "Olinda", "Federico", "Sylvie", "Rodrick", "Sheilah", "Jorge", "Lucrecia",
    "Johnathan", "Penelope", "Albertine", "Melaine", "Shelba", "Kristin",
    "Jenifer", "Jaimee", "Contessa", "Ellis", "Fletcher", "Clint", "Jann",
    "Dudley", "Krystina", "Renaldo", "Phyllis", "Chantell", "Katherine", "Simon",
    "Dwana", "Mckinley", "Kymberly", "Cassy", "Sharen", "Golda", "Catheryn",
    "Erika", "Socorro", "Beatrice", "Marilynn", "Kasha", "Barbera", "Annika",
    "Lorenzo", "Treasa", "Pierre", "Myra", "Irwin", "Sunday", "Lesha", "Roselee",
    "Felix", "Toya", "Donita", "Jeane", "Arron", "Terry", "Candance", "Marita",
    "Rowena", "Ying", "Arleen", "Neil", "Hyman", "Elvira", "Patrica", "Lucie",
    "Misha", "Art", "Jestine", "Shanta", "Angelia", "Curtis", "Jeremiah",
    "Arlean", "Dovie", "Milton", "Emil", "Joette", "Paz", "Odis", "Horacio",
    "Arthur", "Manda", "Leontine", "Debbra", "Lezlie", "Malinda", "Jeffry",
    "Raisa", "Lilliam", "Yulanda", "Felisa", "Mabelle", "Barbara", "Clair",
    "Rikki", "Delisa", "Gillian", "Jodie", "Adriana", "Azzie", "Matilda",
    "Darline", "Euna", "Lurlene", "Michelle", "Juliette", "Tracey", "Derrick",
    "Al", "Mitsue", "Huong", "Susan", "Otelia", "Jeannette", "Denna", "Luciano",
    "Elidia", "Deloise", "Cornelius", "Charlott", "Kirby", "Dominga", "Anabel",
    "Edythe", "Oralia", "Georgeann", "Sona", "Monserrate", "Destiny",
    "Cleopatra", "Donella", "Alyssa", "Myrtice", "Madison", "Keira", "Diane",
    "Clark", "Sheree", "Janyce", "Muoi", "Christel", "Peggie", "Carlee",
    "Christene", "Taryn", "Susanna", "Penny", "Carrol", "June", "Kattie",
    "Aaron", "Cortney", "Franklyn", "Bunny", "Cherryl", "Mike", "Zenia", "Judy",
    "Vella", "Shayna", "Geraldine", "Sixta", "Iraida", "Celena", "Delma",
    "Winona", "Riva", "Christie", "Cheri", "Henrietta", "Giovanni", "Cherise",
    "Lizbeth", "Madelene", "Bradford", "Craig", "Dewitt", "Margurite", "Bari",
    "Reynaldo", "Shad", "Ismael", "Darnell", "Lelia", "Marin", "Cathy", "Rico",
    "Despina", "Ivory", "Dinorah", "Ellena", "Camila", "Sherril", "China",
    "Tania", "Birgit", "Maricruz", "Caitlyn", "Laraine", "Bridgett", "Shante",
    "Galina", "Antionette", "Marvella", "Janis", "Isobel", "Loraine", "Spencer",
    "Lizzette", "Janice", "Steven", "Nydia", "Belle", "Connie", "Magaret",
    "Vilma", "Krysta", "Brendan", "Katherin", "Martha", "Lamar", "Aisha",
    "Kellie", "Fatimah", "Armand", "Dakota", "Kimi", "Dot", "Ami", "Shawn",
    "Ana", "Claudia", "Annemarie", "Aliza", "Belva", "Chan", "Stefan", "Ligia",
    "Guillermo", "Ivy", "Genoveva", "Cathie", "Teresia", "Glenda", "Laveta",
    "Kena", "Lila", "Suk", "Jazmine", "Jerome", "Phylis", "Boyce", "Roni",
    "Reagan", "Wendolyn", "Melita", "Karima", "Mariko", "Veda", "Oscar",
    "Elfrieda", "Jeffrey", "Magdalen", "Angella", "Willodean", "Susanne",
    "Lavonia", "Aurore", "Noble", "Esta", "Roman", "Verda", "Tiffany", "Duane",
    "Karry", "Octavio", "Rochel", "Lorena", "Josefine", "Porfirio", "Antoinette",
    "Andree", "Elisabeth", "Jacquline", "Irish", "Alden", "Ola", "Coreen",
    "Gertrud", "Debbie", "Coralee", "Zelda", "Fidelia", "Nenita", "Monty",
    "Rhiannon", "Nga", "Caleb", "Kurtis", "Nicolette", "Marvin", "Maryetta",
    "Phil", "John", "Numbers", "Tegan", "Eveline", "Gail", "Stacey", "Olga",
    "Brant", "Francoise", "Mikel", "Dionna", "Marion", "Selina", "Kenia",
    "Katharine", "Lashawna", "Hector", "Mark", "Joeann", "Elnora", "Brad",
    "Trey", "Necole", "Edris", "Christa", "Donny", "Johnson", "Peg", "Stephenie",
    "Bernardine", "Chu", "Myrtle", "Narcisa", "Reggie", "Gregorio", "Lanie",
    "Mana", "Alishia", "Filomena", "Ollie", "Bob", "Veta", "Roxanna", "Orville",
    "Alexandria", "Elizbeth", "Cristie", "Sherrill", "Maryjo", "Vivienne",
    "Karleen", "Jodee", "Marquitta", "Dwayne", "Portia", "Vickey", "Verna",
    "Russ", "Shirly", "Rocco", "Signe", "Dolores", "Marianela", "Nancee",
    "Marcelle", "Jere", "Stacee", "Courtney", "Kandace", "Reta", "Billi",
    "Ranee", "Larae", "Glynis", "Arlen", "Carlie", "Ross", "Mayola", "Samira",
    "Delorse", "Indira", "Jama", "Luther", "Gwyneth", "Jefferson", "Zaida",
    "Sonia", "Trisha", "Lovetta", "Cordie", "Chloe", "Cira", "Sharee", "Vernell",
    "Charles", "Cherrie", "Toi", "Gus", "Anette", "Lessie", "Jesusa", "Rolando",
    "Aleida", "Danyell", "Rusty", "Charise", "Amee", "Lonnie", "Adria",
    "Antonio", "Kayleen", "Anissa", "Analisa", "Cyrus", "Meaghan", "Amiee",
    "Chuck", "Marianna", "Katie", "Everett", "Valda", "Margaret", "Corie",
    "Maryln", "Donette", "Donya", "Lashandra", "Willene", "Cleora", "Anya",
    "Dexter", "Jonna", "Martin", "Isabell", "Linda", "Carry", "Etsuko", "Emmie",
    "Marya", "Clarissa", "Darcey", "Yesenia", "Nubia", "Pansy", "Sam",
    "Roosevelt", "Jane", "Tisa", "Gertie", "Daniell", "Mirna", "Iola", "Eloise",
    "Malorie", "Shondra", "Marlyn", "Hildegarde", "Lida", "Joellen", "Frances",
    "Damaris", "Jerri", "Glory", "Trinity", "Dorla", "Romelia", "Jeanne", "Elin",
    "Raul", "Isaura", "Kia", "Mai", "Thao", "Robbin", "Ernestina", "Dorcas",
    "Alan", "Debby", "Cordia", "Claretta", "Carolyn", "Georgine", "Bernardo",
    "Ryan", "Scotty", "Felicidad", "Alfreda", "Idella", "Ingeborg", "Agustina",
    "Kiera", "Sadye", "Rocio", "Torie", "Lizzie", "Josephina", "Timika",
    "Georgiana", "Michaela", "Kathey", "Chanelle", "Conception", "Luetta",
    "Karena", "Geralyn", "Tyree", "Roselia", "Kemberly", "Thelma", "Sandra",
    "Margarete", "Alanna", "Izetta", "Ernesto", "Arianna", "Hai", "Anibal",
    "Chery", "Felipe", "Marianne", "Devona", "Jung", "Rina", "Madaline", "Ossie",
    "Natalya", "Leena", "Macy", "Yolanda", "Natashia", "Clemencia", "Terence",
    "Ronni", "Rosaura", "Dedra", "Barney", "Alton", "Ricarda", "Ji", "Freddie",
    "Gracia", "Svetlana", "Season", "Alma", "Britteny", "Jacquetta", "Glen",
    "Latina", "Juana", "Augustine", "Claude", "Marian", "Charlie", "Latashia",
    "Brooke", "Nickole", "Gilberto", "Veronika", "Kimbra", "Claretha", "Kathrin",
    "Marivel", "Andres", "Miranda", "Mickey", "Tamara", "Olene", "Pattie",
    "Keli", "Avis", "Luci", "Adrien", "Alida", "Felicitas", "Julene", "Janeen",
    "Edith", "Harlan", "Jerrold", "Jinny", "Han", "Hiram", "Elba", "Penni",
    "Hellen", "Wanita", "Pauletta", "Carlotta", "Chrissy", "Nickolas", "Ignacio",
    "Ines", "Epifania", "Carylon", "Mae", "Britney", "Glinda", "Becky", "Debra",
    "Dick", "Genevive", "Guadalupe", "Melina", "Leslie", "Erick", "Denice",
    "Cliff", "Valorie", "Valeria", "Beverley", "Dixie", "Annice", "Winfred",
    "Carli", "Maira", "Adelaida", "Winnifred", "Michele", "Max", "Florinda",
    "Delphia", "Yessenia", "Dara", "Alejandro", "Kelley", "Sanford", "Karrie",
    "Kitty", "Philomena", "Awilda", "Lelah", "Farah", "Deeanna", "Hillary",
    "Tamera", "Doretta", "Tynisha", "Jean", "Debi", "Valrie", "Otha", "Ja",
    "Melvin", "Gisele", "Kathline", "Walter", "Carmel", "Paulina", "Tracie",
    "Robt", "Lawanda", "Rosina", "Vania", "Jonathan", "Nyla", "Vonnie", "Vennie",
    "Anne", "Yong", "Alethea", "Rickie", "Jacquie", "Annelle", "Angelita",
    "Siobhan", "Nancie", "Whitney", "Karlene", "Penney", "Olympia", "Rose",
    "Jerrie", "Leisha", "Arnoldo", "Deon", "Luna", "Maida", "Burton", "Ashlie",
    "Delilah", "Marvel", "Chang", "Lourdes", "Elma", "Emilio", "Marcene",
    "Maranda", "Annalisa", "Arlie", "Mathew", "Cody", "Carlyn", "Telma",
    "Diamond", "Ardell", "Ophelia", "Porter", "Geneva", "Herta", "Jared",
    "Vallie", "Mariano", "Crystle", "Cher", "Cierra", "Nicole", "Particia",
    "Teena", "Elvin", "Nohemi", "Coral", "Kallie", "Lacie", "Madonna",
    "Napoleon", "Roberta", "Deneen", "Alec", "Hsiu", "Tomika", "Leigha",
    "Shantelle", "Shu", "Nelda", "Mellisa", "Jamel", "Dawna", "Rashad",
    "Armanda", "Sook", "Qiana", "Dee", "Thora", "Lamont", "Shirlee", "Audra",
    "Margherita", "Bridgette", "Daria", "Kyra", "Elden", "Dorsey", "Jayna",
    "Lakenya", "Sasha", "Shameka", "Cecilia", "Mason", "Lilia", "Lashanda",
    "Evelina", "Quyen", "Tony", "Retha", "Wally", "Kiara", "Deandra", "Verdie",
    "Kathi", "Kelsey", "Dwain", "Corina", "Ina", "Erline", "Jacki", "Anja",
    "Ashanti", "Helena", "Rossana", "Rosalyn", "Echo", "Sheridan", "Elaine",
    "Camilla", "Sharika", "Kit", "Leonia", "Marx", "Lanny", "Josefina", "Renita",
    "Lorene", "Mitsuko", "Nikole", "Jadwiga", "Adelia", "Elaina", "Janella",
    "Darryl", "Shaina", "Britta", "Darcel", "Linn", "Niesha", "Charolette",
    "Gricelda", "Marquerite", "Lakendra", "Myong", "Valencia", "Alice", "Vanita",
    "Carisa", "Gaylene", "Andria", "Clelia", "Mary", "Lai", "Concepcion",
    "Keitha", "Karlyn", "Reynalda", "Cheryl", "Dora", "Mozella", "Jana", "Jule",
    "Jerrod", "Cecila", "Maura", "Hiroko", "Madlyn", "Phyliss", "Wendie", "Elsa",
    "Mamie", "Clara", "Morgan", "Eileen", "Roxana", "Joel", "Genaro", "Clyde",
    "Simonne", "Sophie", "Shella", "Pricilla", "Oren", "Drucilla", "Adele",
    "Louise", "Layla", "Honey", "Arie", "Katia", "Jamee", "Lynell", "Delores",
    "Leanne", "Jeannetta", "Audrie", "Eldora", "Ula", "Jarred", "Shiela",
    "Leatrice", "Audrey", "Emogene", "Meagan", "Camille", "Magnolia", "Lloyd",
    "Carla", "Clorinda", "Domingo", "Ching", "Lue", "Malissa", "Marylin", "Aiko",
    "Moses", "Laverne", "Henry", "Trevor", "Lawrence", "Julienne", "Colleen",
    "Liana", "Laticia", "Jeanice", "Laverna", "Will", "Maurice", "Ginger",
    "Clemmie", "Joye", "Lavona", "Jacinta", "Whitley", "Letisha", "Thaddeus",
    "Deedra", "Denyse", "Nina", "Marquita", "Ezequiel", "Rosaria", "Faye",
    "Dusty", "Tod", "Karan", "Cassey", "Eli", "Hank", "Margene", "Meri",
    "Mikaela", "Johnsie", "Sammie", "Roxy", "Pearl", "Angla", "Ming", "Kayla",
    "Cornell", "Virginia", "Armida", "Tammera", "Cristin", "Roslyn", "Tyrone",
    "Kristeen", "Malcom", "Enoch", "Issac", "Ruthann", "Duncan", "Tessie",
    "Rufina", "Carroll", "Cathern", "Nam", "Ema", "Rosemarie", "Adrian", "Latia",
    "Saul", "Missy", "Christi", "Ebony", "Tina", "Nadene", "Keiko", "Felicita",
    "Elisa", "Kanesha", "Saundra", "Ethelene", "Giuseppina", "Stanton", "Kathie",
    "Maurine", "Daine", "Idell", "Isaias", "Kai", "Tiana", "Alvin", "Cathleen",
    "Julieann", "Kalyn", "Amalia", "Haley", "Maurita", "Allegra", "Shelli",
    "Jenee"
  };

  private static final String[] randomLastNames =
  {
    "Payne", "Pace", "Peralta", "Valencia", "Berry", "Bowman", "Lawson", "Tyler",
    "Mccoy", "Mcdowell", "Brennan", "Everett", "Zuniga", "Mccullough", "Briggs",
    "Zamora", "Ochoa", "Gallegos", "Best", "Elliott", "Love", "Simpson",
    "Foster", "Peck", "Lynn", "Estrada", "Drake", "Huber", "Becker", "Burns",
    "Strong", "Fisher", "Hayden", "Quinn", "Bentley", "Cuevas", "Sanchez",
    "Sweeney", "Heath", "Vang", "Hebert", "Boyle", "Carrillo", "Mclean", "Combs",
    "Levy", "Little", "Daniel", "Carey", "Bates", "Mcintosh", "Duarte", "King",
    "Lara", "Erickson", "Ibarra", "Sanders", "Mcconnell", "Phan", "Leon",
    "Weber", "Reeves", "Schwartz", "Alvarado", "Rasmussen", "Mitchell", "Barton",
    "Gutierrez", "Blake", "Landry", "Yoder", "Allison", "Jennings", "Browning",
    "Hartman", "Richards", "Green", "Garrett", "Morton", "Fuller", "Dorsey",
    "Bryant", "Wells", "Fleming", "Miller", "Galindo", "Miranda", "Clarke",
    "Pugh", "Riley", "Haynes", "Decker", "Davila", "French", "Hurst", "Barajas",
    "Dillon", "Harris", "Parra", "Lee", "Ali", "Burke", "Dougherty", "Bullock",
    "Woods", "Murphy", "Hendrix", "Ahmed", "Robinson", "Oliver", "Pittman",
    "English", "Maldonado", "Saunders", "Koch", "Morrison", "Poole", "Gibson",
    "Kelley", "Solomon", "Conley", "Michael", "Novak", "Melton", "Holloway",
    "Luna", "Whitney", "Vazquez", "Santos", "Avalos", "Davidson", "Walter",
    "Dyer", "Burgess", "Logan", "Sawyer", "Randall", "Dixon", "Mcclure",
    "Francis", "Santana", "Huerta", "Brown", "Higgins", "Blevins", "Chavez",
    "Tucker", "Cook", "Lowery", "Dalton", "Vo", "Kerr", "Bond", "Cervantes",
    "Winters", "Scott", "Warner", "Willis", "Barber", "Lawrence", "Duffy",
    "Schmidt", "Orozco", "Mcgee", "Wright", "Lucero", "Felix", "Price", "Reilly",
    "Hoover", "Hancock", "Turner", "Beard", "Hood", "Pacheco", "Singh", "Floyd",
    "Taylor", "Austin", "Harvey", "Whitehead", "Melendez", "Lester", "Gallagher",
    "Munoz", "Cole", "Rush", "Ponce", "Barry", "Robertson", "Castillo", "Cantu",
    "Cantrell", "Cherry", "Sutton", "Martin", "Alexander", "Dunn", "Gaines",
    "Arnold", "Abbott", "House", "Terry", "Mcdaniel", "Lyons", "Rogers",
    "Fletcher", "Keller", "Garcia", "Hogan", "Deleon", "Oneal", "Murillo",
    "Booker", "Maxwell", "Mora", "Schmitt", "Le", "Kim", "Benitez", "Dejesus",
    "Nolan", "Romero", "Jordan", "Larson", "Caldwell", "Ray", "Ho", "Prince",
    "Hernandez", "Salgado", "Parsons", "Macdonald", "Fitzpatrick", "Mosley",
    "Hinton", "Simon", "Clark", "Rangel", "Mclaughlin", "Villanueva", "Greene",
    "Singleton", "Myers", "Summers", "Glenn", "Garrison", "Oconnell", "May",
    "Marshall", "Klein", "Tate", "Duke", "Mercado", "Park", "Villarreal", "Wang",
    "Parrish", "Mcmillan", "Espinosa", "Hammond", "Bowers", "Salazar", "Vargas",
    "Villa", "Castaneda", "Wiley", "Sellers", "Franco", "Correa", "Bernard",
    "Weiss", "Barrera", "Watts", "Williams", "Lim", "Odom", "Coffey", "Ford",
    "Xiong", "Mcintyre", "Good", "Herman", "Mason", "Quintana", "Farmer", "Wall",
    "Donovan", "Gonzales", "Knight", "Ingram", "Sierra", "Craig", "Wood",
    "Chambers", "Arias", "Figueroa", "Odonnell", "Ayers", "Mayo", "Hess",
    "Powell", "Owen", "Gray", "Jaramillo", "Hutchinson", "Curry", "Hodge",
    "Palmer", "Lambert", "Cano", "Jackson", "Hensley", "Gregory", "Mccarthy",
    "Wheeler", "Wong", "Lucas", "Medina", "Enriquez", "Howe", "Gonzalez",
    "Massey", "Guevara", "Ryan", "Nelson", "Contreras", "Lindsey", "Moody",
    "Cardenas", "Sanford", "Frank", "Cobb", "Ayala", "Buck", "David", "Pratt",
    "Harrison", "Brandt", "Page", "Mcfarland", "Barnett", "Kaur", "Thornton",
    "Espinoza", "Campos", "Trevino", "Arellano", "Cross", "Waller", "Dickson",
    "Frye", "Glover", "Reyna", "Orr", "Hanna", "Arroyo", "Petersen", "Mueller",
    "Molina", "Hardin", "Jacobs", "Yu", "Valdez", "Bass", "Woodward", "Bailey",
    "Goodwin", "White", "Horne", "Velazquez", "Johnson", "Dean", "Farley",
    "Adkins", "Allen", "Montes", "Boyer", "Larsen", "Herring", "Truong", "Yates",
    "Rivers", "Davis", "Paul", "Ruiz", "Ballard", "Mccormick", "Barr", "Castro",
    "Golden", "Hampton", "Burton", "Eaton", "Savage", "Beltran", "Howard",
    "Carroll", "Ortega", "Richard", "Ward", "Crawford", "Patrick", "Underwood",
    "Cortes", "Diaz", "Proctor", "Mullen", "Kline", "Guerrero", "Jefferson",
    "Mathews", "Wallace", "Krueger", "Lloyd", "Hale", "Khan", "Mccall", "George",
    "Buchanan", "Montgomery", "Nava", "West", "Merritt", "Maynard", "Moon",
    "Hurley", "Kirk", "Kelly", "Ellis", "Faulkner", "Baxter", "Vaughn", "Bush",
    "Vaughan", "Reese", "Rodriguez", "Sloan", "Small", "Cisneros", "Stone",
    "Mcpherson", "Sampson", "Dudley", "Keith", "Edwards", "Ventura", "Mays",
    "Morse", "Black", "Coleman", "Church", "Moss", "Patterson", "Duran",
    "Richardson", "Hayes", "Stephenson", "Reyes", "Spencer", "Kirby", "Lin",
    "Pierce", "Roman", "Vincent", "Hardy", "Blair", "Shaffer", "Newton", "Ball",
    "Rosario", "Knox", "Gardner", "Beil", "Cordova", "Gross", "Mendez", "Olson",
    "Maddox", "Bradley", "Nunez", "Tapia", "Mata", "Robles", "Sharp", "Cox",
    "Woodard", "Stevens", "Sosa", "Morrow", "Thompson", "Bishop", "Sexton",
    "Tran", "Wise", "Bell", "Long", "Beck", "Kent", "Nicholson", "Rhodes",
    "Chase", "Hobbs", "Carpenter", "Stein", "Gilbert", "Aguirre", "Sandoval",
    "Walker", "Perkins", "Carlson", "Oneill", "Lamb", "Estes", "Humphrey",
    "Lane", "Douglas", "Aguilar", "Christensen", "Conrad", "Moore", "Benton",
    "Marquez", "Cameron", "Sparks", "Matthews", "Roach", "Mckee", "Avila",
    "Galvan", "Holmes", "Hughes", "Murray", "Valenzuela", "Lowe", "Villegas",
    "Shields", "Jacobson", "Bonilla", "Perry", "Ramirez", "Ferguson", "Gould",
    "Frazier", "Henry", "Hail", "Walls", "Hall", "Atkinson", "Johns", "Carson",
    "Rice", "Shah", "Cohen", "Rodgers", "Friedman", "Calderon", "Hodges",
    "Rivera", "Neal", "Kane", "Flowers", "Rocha", "Bender", "Calhoun", "Rollins",
    "York", "Phillips", "Banks", "Garza", "Foley", "Middleton", "Snow", "Knapp",
    "Conner", "Juarez", "Huang", "James", "Watson", "Wilkerson", "Benson",
    "Stephens", "Blackwell", "Baker", "Beasley", "Cabrera", "Day", "Livingston",
    "Durham", "Trujillo", "Hamilton", "Wilkins", "Smith", "Zavala", "Mills",
    "Brock", "Osborne", "Pennington", "Butler", "Russo", "Gordon", "Horton",
    "Giles", "Huynh", "Dodson", "Williamson", "Cain", "Mckinney", "Webster",
    "Bruce", "Benjamin", "Silva", "Mcguire", "Leach", "Leblanc", "Weaver",
    "Delgado", "Leonard", "Booth", "Boyd", "Shannon", "Escobar", "Morgan",
    "Marsh", "Pena", "Travis", "Powers", "Meyers", "Salinas", "Bradshaw",
    "Reynolds", "Meyer", "Baldwin", "Bennett", "Donaldson", "Bartlett",
    "Velasquez", "Acosta", "Delacruz", "Gilmore", "Gillespie", "Cooper",
    "Griffith", "Shaw", "Joseph", "Finley", "Haley", "Navarro", "Pineda",
    "Perez", "Dickerson", "Shepherd", "Schaefer", "Bean", "Valentine", "Harding",
    "Medrano", "Leal", "Adams", "Lewis", "Sims", "Harmon", "Russell", "Cortez",
    "Cannon", "Andrews", "Carter", "Short", "Bernal", "Bradford", "Rowland",
    "Vasquez", "Chen", "Moran", "Zhang", "Steele", "Phelps", "Christian",
    "Fitzgerald", "Stuart", "Marks", "Greer", "Barker", "Bauer", "Guzman",
    "Lynch", "Hansen", "Flynn", "Mendoza", "Jenkins", "Kennedy", "Mcmahon",
    "Lugo", "Mathis", "Rich", "Rojas", "Blanchard", "Nixon", "Stewart",
    "Quintero", "Hines", "Johnston", "Sullivan", "Olsen", "Dunlap", "Mccarty",
    "Fowler", "Ross", "Cummings", "Patton", "Cochran", "Peterson", "Jensen",
    "Ware", "Hopkins", "Henson", "Avery", "Chandler", "Holt", "Mckay",
    "Mckenzie", "Freeman", "Magana", "Potts", "Wu", "Davenport", "Schneider",
    "Young", "Jones", "Spence", "Chang", "Meza", "Esparza", "Tang", "Macias",
    "Doyle", "Houston", "Whitaker", "Spears", "Rios", "Randolph", "Walters",
    "Carr", "Ortiz", "Manning", "Rubio", "Schroeder", "Hunt", "Vega", "Hart",
    "Terrell", "Herrera", "Monroe", "Roberts", "Oconnor", "Peters", "Welch",
    "Patel", "Franklin", "Brewer", "Nguyen", "Mcbride", "Armstrong",
    "Strickland", "Parks", "Holland", "Bravo", "Noble", "Grant", "Atkins",
    "Case", "Sherman", "Kramer", "Liu", "Hull", "Walton", "Boone", "Watkins",
    "Soto", "Burnett", "Fischer", "Mejia", "Lopez", "Shelton", "Hudson",
    "Mullins", "Dawson", "Harper", "Roberson", "Duncan", "Porter", "Gates",
    "Morris", "Skinner", "Solis", "Huffman", "Padilla", "Colon", "Graves", "Lam",
    "Acevedo", "Owens", "Copeland", "Wagner", "Moses", "Collins", "Collier",
    "Fernandez", "Washington", "Harrell", "Andersen", "Parker", "Goodman",
    "Huff", "Mcclain", "Fuentes", "Hester", "Suarez", "Wyatt", "Portillo",
    "Bridges", "Mayer", "Mack", "Moyer", "Esquivel", "Snyder", "Anthony", "Gill",
    "Bowen", "Richmond", "Thomas", "Anderson", "Townsend", "Wade", "Stanton",
    "Raymond", "Hoffman", "Bryan", "Blackburn", "Stout", "Berger", "Roth",
    "Walsh", "Clements", "Velez", "Barron", "Brady", "Yang", "Pearson", "Horn",
    "Choi", "Palacios", "Gomez", "Guerra", "Rose", "Hanson", "Santiago",
    "Jarvis", "Newman", "Reid", "Obrien", "Pope", "Wolfe", "Branch", "Trejo",
    "Norton", "Henderson", "Stokes", "Mccann", "Dominguez", "Corona",
    "Wilkinson", "Harrington", "Cruz", "Casey", "Moreno", "Reed", "Flores",
    "Conway", "Serrano", "Camacho", "Dennis", "Schultz", "Madden", "Roy",
    "Griffin", "Frost", "Charles", "Lu", "Mahoney", "Ramos", "Barrett",
    "Wiggins", "Crosby", "Callahan", "Pham", "Vu", "Costa", "Ellison", "Webb",
    "Mann", "Lozano", "Burch", "Wolf", "Hicks", "Farrell", "Robbins", "Shepard",
    "Jimenez", "Fields", "Pruitt", "Stark", "Andrade", "Simmons", "Li", "Curtis",
    "Mcdonald", "Stevenson", "Grimes", "Bautista", "Nielsen", "Salas", "Clay",
    "Graham", "Nash", "Hickman", "Buckley", "Cunningham", "Brooks", "Swanson",
    "Chapman", "Weeks", "Campbell", "Ramsey", "Barnes", "Torres", "Glass",
    "Rowe", "Gibbs", "Todd", "Lang", "Vance", "Malone", "Gentry", "Potter",
    "Warren", "Marin", "Frederick", "Norris", "Clayton", "Norman", "Cline",
    "Crane", "Wilson", "Preston", "Blankenship", "Evans", "Morales", "Rosas",
    "Compton", "Delarosa", "Stanley", "Zimmerman", "Fry", "Villalobos", "Hahn",
    "Nichols", "Montoya", "Rivas", "Sheppard", "Daniels", "Pitts", "Martinez",
    "Chung", "Hawkins", "Ashley", "Hunter", "Tanner", "Rosales", "Waters",
    "Garner", "Howell", "Person", "Byrd", "Alfaro", "Pollard", "Kemp", "Hill",
    "Hubbard", "Alvarez", "Daugherty", "Wilcox", "Chan", "Meadows", "Fox",
    "Hendricks", "Berg", "Stafford", "Miles"
  };

  public static String getRandomFirstName(Integer s)
  {
    if(s == null)
    {
      s = new Random().nextInt();
    }
    int i = s.hashCode() % randomFirstNames.length;
    return randomFirstNames[i];
  }

  public static String getRandomLastName(Integer s)
  {
    if(s == null)
    {
      s = new Random().nextInt();
    }
    int i = s.hashCode() % randomLastNames.length;
    return randomLastNames[i];
  }

  public static String getRandomLoginName(Integer s)
  {
    if(s == null)
    {
      s = new Random().nextInt();
    }
    int i = s.hashCode() % randomFirstNames.length;
    int j = s.hashCode() % randomLastNames.length;
    return randomFirstNames[i].substring(0, 1).toLowerCase()
            + randomLastNames[j].substring(0, 1).toLowerCase()
            + String.valueOf(100000 + new Random(s).nextInt(900000));
  }

  public static String getRandomEmail(Integer s)
  {
    if(s == null)
    {
      s = new Random().nextInt();
    }
    int i = s.hashCode() % randomFirstNames.length;
    int j = s.hashCode() % randomLastNames.length;
    return randomFirstNames[i] + "." + randomLastNames[j] + "@tuni.fi";
  }

  public static String getRandomStudentNumber(Integer s)
  {
    if(s == null)
    {
      s = new Random().nextInt();
    }
    return String.valueOf(100000 + new Random(s).nextInt(900000));
  }
   */

  private static final Logger logger = Logger.getLogger(WetoUtilities.class);
  private static final int bufferSize = 2048;

  public static String getPageContent(CloseableHttpClient httpClient,
          HttpClientContext httpContext, String url)
          throws Exception
  {
    StringBuilder resultBuffer = new StringBuilder();
    HttpGet request = new HttpGet(url);
    RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(5000).setConnectTimeout(5000)
            .setSocketTimeout(5000).setCookieSpec(
            CookieSpecs.DEFAULT).build();
    request.setConfig(requestConfig);
    request.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0");
    try(CloseableHttpResponse response = httpClient
            .execute(request, httpContext))
    {
      HttpEntity entity = response.getEntity();
      if(entity != null)
      {
        try(BufferedReader in = new BufferedReader(new InputStreamReader(entity
                .getContent())))
        {
          String inputLine;
          while((inputLine = in.readLine()) != null)
          {
            resultBuffer.append(inputLine).append("\n");
          }
        }
      }
    }
    return resultBuffer.toString();
  }

  public static String submitForm(CloseableHttpClient httpClient,
          HttpClientContext httpContext, String url, final String parString)
          throws Exception
  {
    StringBuilder resultBuffer = new StringBuilder();
    HttpPost request = new HttpPost(url);
    RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(5000).setConnectTimeout(5000)
            .setSocketTimeout(5000).setCookieSpec(
            CookieSpecs.DEFAULT).build();
    request.setConfig(requestConfig);
    request.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0");
    request.addHeader(HttpHeaders.ACCEPT,
            "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US, en, fi-FI, fi");
    request.addHeader(HttpHeaders.HOST, new URI(url).getHost());
    request.addHeader(HttpHeaders.REFERER, url);
    request.addHeader(HttpHeaders.CONTENT_TYPE,
            "application/x-www-form-urlencoded");
    AbstractHttpEntity parEntity = new AbstractHttpEntity()
    {

      public boolean isRepeatable()
      {
        return false;
      }

      public long getContentLength()
      {
        return -1;
      }

      public boolean isStreaming()
      {
        return false;
      }

      public InputStream getContent() throws IOException
      {
        throw new UnsupportedOperationException();
      }

      public void writeTo(final OutputStream outstream) throws IOException
      {
        Writer writer = new OutputStreamWriter(outstream, "UTF-8");
        writer.write(parString);
        writer.flush();
      }

    };
    request.setEntity(parEntity);
    try(CloseableHttpResponse response = httpClient
            .execute(request, httpContext))
    {
      HttpEntity entity = response.getEntity();
      if(entity != null)
      {
        try(BufferedReader in = new BufferedReader(new InputStreamReader(entity
                .getContent())))
        {
          String inputLine;
          while((inputLine = in.readLine()) != null)
          {
            resultBuffer.append(inputLine).append("\n");
          }
        }
      }
    }
    return resultBuffer.toString();
  }

  private static class ZipVisitor extends SimpleFileVisitor<Path>
  {
    private final Path baseDir;
    private final ZipOutputStream zos;
    private final byte[] readBuffer;

    ZipVisitor(Path baseDir, ZipOutputStream zos) throws IOException
    {
      this.baseDir = baseDir;
      this.zos = zos;
      readBuffer = new byte[bufferSize];
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            throws IOException
    {
      try(InputStream fis = new BufferedInputStream(new FileInputStream(file
              .toFile())))
      {
        ZipEntry anEntry = new ZipEntry(baseDir.relativize(file).toString());
        //place the zip entry in the ZipOutputStream object
        zos.putNextEntry(anEntry);
        //now write the content of the file to the ZipOutputStream
        int bytesIn = 0;
        while((bytesIn = fis.read(readBuffer)) != -1)
        {
          zos.write(readBuffer, 0, bytesIn);
        }
        zos.closeEntry();
      }
      return FileVisitResult.CONTINUE;
    }

  }

  // May have problems with file name encodings. Prefer to use zipSubdir?
  public static void zipDir(File baseDir, File dirToZip, OutputStream zip,
          Integer level)
          throws FileNotFoundException, IOException
  {
    try(ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(zip)))
    {
      if(level != null)
      {
        zos.setLevel(level);
      }
      Files.walkFileTree(dirToZip.toPath(),
              new ZipVisitor(baseDir.toPath(), zos));
    }
  }

  public static void zipSubDir(File baseDir, String subDirToZip, File zipFile,
          boolean doNotCompress)
          throws WetoActionException, IOException, InterruptedException
  {
    Path tmpZipDir = Files.createTempDirectory("weto");
    File workZipFile = new File(tmpZipDir.toFile(), "tmp.zip");
    ProcessBuilder pb;
    if(doNotCompress)
    {
      pb = new ProcessBuilder("zip", "-0", "-r", workZipFile.toString(),
              subDirToZip);
    }
    else
    {
      pb = new ProcessBuilder("zip", "-r", workZipFile.toString(), subDirToZip);
    }
    pb.directory(baseDir);
    pb.redirectErrorStream(true);
    Process p = pb.start();
    InputStreamReader pout = new InputStreamReader(p.getInputStream(), "UTF-8");
    char[] buf = new char[16384];
    while(p.isAlive())
    {
      pout.read(buf, 0, buf.length);
    }
    if(p.exitValue() != 0)
    {
      throw new WetoActionException("Zip error " + p.exitValue() + " running: "
              + workZipFile.toString()
              + subDirToZip);
    }
    Files.move(workZipFile.toPath(), zipFile.toPath(),
            StandardCopyOption.REPLACE_EXISTING);
    deleteRecursively(tmpZipDir.toFile());
  }

  public static void zipFile(File baseDir, File fileToZip, OutputStream zip,
          Integer level)
          throws FileNotFoundException, IOException
  {
    try(ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(zip)))
    {
      if(level != null)
      {
        zos.setLevel(level);
      }
      new ZipVisitor(baseDir.toPath(), zos).visitFile(fileToZip.toPath(), null);
    }
  }

  public static void unzipFile(File fileToUnzip, File unzipDir)
          throws ZipException, IOException
  {
    byte[] buffer = new byte[bufferSize];
    try(ZipFile zip = new ZipFile(fileToUnzip))
    {
      final Path unzipPath = unzipDir.getAbsoluteFile().toPath();
      Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();
      // Process each entry
      while(zipFileEntries.hasMoreElements())
      {
        ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
        String currentEntry = entry.getName();
        File destFile = new File(unzipDir, currentEntry);
        File destParent = destFile.getParentFile();
        if(destParent.getAbsoluteFile().toPath().startsWith(unzipPath))
        {
          // Create the parent directory structure if needed
          destParent.mkdirs();
          if(!entry.isDirectory())
          {
            try(BufferedInputStream is = new BufferedInputStream(zip
                    .getInputStream(entry)))
            {
              FileOutputStream fos = new FileOutputStream(destFile);
              try(BufferedOutputStream dest = new BufferedOutputStream(fos,
                      bufferSize))
              {
                int bytesRead = 0;
                while((bytesRead = is.read(buffer)) != -1)
                {
                  dest.write(buffer, 0, bytesRead);
                }
              }
            }
          }
        }
      }
    }
  }

  public static void deleteRecursively(File file)
  {
    if(file.isDirectory())
    {
      for(File contained : file.listFiles())
      {
        deleteRecursively(contained);
      }
    }
    if(!file.delete())
    {
      logger.debug("Unable to delete file: " + file);
    }
  }

  public static void streamToFile(InputStream in, File dst) throws IOException
  {
    try(OutputStream out = new FileOutputStream(dst))
    {
      byte[] buf = new byte[bufferSize];
      int bytesRead;
      while((bytesRead = in.read(buf)) > 0)
      {
        out.write(buf, 0, bytesRead);
      }
    }
    finally
    {
      in.close();
    }
  }

  public static void fileToStream(File file, OutputStream out)
          throws IOException
  {
    try(InputStream in = new BufferedInputStream(new FileInputStream(file)))
    {
      byte[] buf = new byte[bufferSize];
      int bytesRead;
      while((bytesRead = in.read(buf)) > 0)
      {
        out.write(buf, 0, bytesRead);
      }
    }
    finally
    {
      out.close();
    }
  }

  public static String stringToGzippedBase64(String in)
          throws UnsupportedEncodingException, IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try(GZIPOutputStream gzip = new GZIPOutputStream(baos))
    {
      gzip.write(in.getBytes("UTF-8"));
    }
    return DatatypeConverter.printBase64Binary(baos.toByteArray());
  }

  public static String gzippedBase64ToString(String gb64)
          throws UnsupportedEncodingException, IOException
  {
    byte[] bytes = DatatypeConverter.parseBase64Binary(gb64);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
    try(GZIPInputStream gzip = new GZIPInputStream(bais))
    {
      byte[] buffer = new byte[bufferSize];
      int bytesRead = 0;
      while((bytesRead = gzip.read(buffer)) != -1)
      {
        baos.write(buffer, 0, bytesRead);
      }
    }
    return baos.toString("UTF-8");
  }

  public static String fileToBase64(File in) throws IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try(BufferedInputStream fileStream = new BufferedInputStream(
            new FileInputStream(in)))
    {
      byte[] buffer = new byte[bufferSize];
      int bytesRead = 0;
      while((bytesRead = fileStream.read(buffer)) != -1)
      {
        baos.write(buffer, 0, bytesRead);
      }
    }
    return DatatypeConverter.printBase64Binary(baos.toByteArray());
  }

  public static String escapeHtml(String str)
  {
    String result = null;
    if(str != null)
    {
      result = StringEscapeUtils.escapeHtml(str);
      // The above does not encode an apostrophe: it must be done separately
      result = result.replace("'", "&#39;");
    }
    return result;
  }

  private static Properties messageResources;

  public static String getMessageResource(String property)
  {
    if(messageResources == null)
    {
      messageResources = new Properties();
      InputStream inputStream = WetoUtilities.class.getResourceAsStream(
              "/MessageResources.properties");
      // Read properties.
      try(BufferedReader propsReader = new BufferedReader(new InputStreamReader(
              inputStream)))
      {
        messageResources.load(propsReader);
      }
      catch(Exception e)
      {
      }
    }
    return messageResources.getProperty(property);
  }

  private static Properties packageResources;

  public static String getPackageResource(String property)
  {
    if(packageResources == null)
    {
      packageResources = new Properties();
      InputStream inputStream = WetoUtilities.class.getClassLoader()
              .getResourceAsStream("package.properties");
      // Read properties.
      try(BufferedReader propsReader = new BufferedReader(new InputStreamReader(
              inputStream)))
      {
        packageResources.load(propsReader);
      }
      catch(Exception e)
      {
      }
    }
    return packageResources.getProperty(property);
  }

  public static boolean validateLoginName(String loginName)
  {
    boolean result = false;
    if(loginName != null)
    {
      final int len = loginName.length();
      if(len >= 2)
      {
        result = true;
        for(int i = 0; i < len; ++i)
        {
          if(!Character.isLetterOrDigit(loginName.charAt(i)))
          {
            result = false;
            break;
          }
        }
        if(!result)
        {
          try
          {
            new InternetAddress(loginName).validate();
            result = true;
          }
          catch(AddressException e)
          {
          }
        }
      }
    }
    return result;
  }

  /**
   * @param username
   * @param isLocalUser
   * @return <i>studentNumber</i> converted from loginName, <i>null</i> if
   * conversion fails or <i>empty string</i> if user is local user.
   */
  public static String getStudentNumberFromLoginName(String username,
          boolean isLocalUser)
  {
    if(isLocalUser)
    {
      // User from local database
      return "";
    }
    else if(username.length() == 7 || username.length() == 8)
    {
      if(username.startsWith("a") && username.substring(1).matches("\\d++"))
      {
        // Open university student
        return username.toUpperCase();
      }
      else if(username.substring(2).matches("\\d+"))
      {
        // Normal student
        return username.substring(2);
      }
    }
    // Something strange in login name
    return null;
  }

  public static String correctEncoding(byte[] bytearray)
  {
    byte[] newByteArray = new byte[bytearray.length];
    byte previousByte = 0;
    int newLen = 0;
    for(byte currentByte : bytearray)
    {
      if(currentByte != -62 && previousByte != -125)
      {
        newByteArray[newLen++] = currentByte;
      }
      else
      {
        newLen--;
      }
      previousByte = currentByte;
    }
    return new String(newByteArray, 0, newLen);
  }

}

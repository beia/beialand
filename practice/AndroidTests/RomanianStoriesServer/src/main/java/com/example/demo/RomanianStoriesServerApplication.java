package com.example.demo;

import com.example.demo.model.Author;
import com.example.demo.model.Story;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.StoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class RomanianStoriesServerApplication implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final StoryRepository storyRepository;

    public RomanianStoriesServerApplication(AuthorRepository authorRepository, StoryRepository storyRepository) {
        this.authorRepository = authorRepository;
        this.storyRepository = storyRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(RomanianStoriesServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        List<Author> authors = List.of(
                Author.builder()
                        .name("Francoise Dolto")
                        .description("Este, alaturi de Jacques Lacan, una dintre personalitatile de renume ale psihanalizei franceze. Si-a inceput activitatea inainte de cel de-al doilea razboi mondial la spitalul Bretonneau, sub indrumarea doctorului Edouard Pichon, presedinte al Societatii Psihanalitice din Paris. A abordat cu precadere probleme legate de dezvoltarea copiilor, aplicind metode psihanalitice de diagnostic si tratament in tulburarile psiho-afective si de comportament ale preadolescentilor.\n" +
                                "\n" +
                                "\n" +
                                "\n" +
                                "\n" +
                                "Opere publicate:\n" +
                                "Psychanalyse et pediatrie, 1971\n" +
                                "L'Eveil de l'esprit chez l'enfant, 1977\n" +
                                "La Foi au risque de la psychanalyse, 1980\n" +
                                "L'Evangile au risque de la psychanalyse, 1980–1982\n" +
                                "La Cause des enfants, 1985\n" +
                                "Tout est langage, 1987\n" +
                                "L'Enfant du miroir, 1987\n" +
                                "La Cause des adolescents, 1989\n" +
                                "\n" +
                                "Web:\n" +
                                "www.francoise-dolto.com ")
                        .pictureUrl("https://cdn.radiofrance.fr/s3/cruiser-production/2018/06/7ba89158-4c86-41ad-aa45-8e37402220ae/838_gettyimages-110144286.jpg")
                        .build(),
                Author.builder()
                        .name("Garcia Marquez")
                        .description("Gabriel García Márquez (n. 6 martie 1927, Aracataca, Columbia - d. 17 aprilie 2014, Ciudad de México, Mexic) a fost un scriitor columbian, laureat al Premiului Nobel pentru Literatură în anul 1982, pentru roman și proză scurtă, în care fantasticul și realul sunt combinate într-o lume liniștită de bogată imaginație, reflectând viața și conflictele unui continent. Este cunoscut de către prieteni drept Gabo.")
                        .pictureUrl("https://www.epl.org/wp-content/uploads/2014/04/marquez-gabriel-adv-obit-slide-lp84-superjumbo-500x333.jpg")
                        .build(),
                Author.builder()
                        .name("Guillaume Musso")
                        .description("Guillaume Musso este unul dintre cei mai populari scriitori francezi contemporani. Romanele lui au fost traduse în 40 de limbi și s-au vândut în peste 30 de milioane de exemplare.\n" +
                                "\n" +
                                "S-a născut la Antibes în 1974 și a studiat științe economice. Mama lui, bibliotecară, i-a cultivat gustul pentru literatură, hrănindu-l cu „Proust la biberon“. La 19 ani, Musso a plecat în Statele Unite ale Americii și s-a îndrăgostit de New York. S-a întors în Franţa plin de idei pentru romanele sale. La 24 de ani, a suferit un grav accident de maşină, care l-a făcut să reflecteze la sensul vieţii şi la posibilitatea ca ea să se sfârşească pe neaşteptate.\n" +
                                "\n" +
                                "A scris apoi într-un suflet 80 de pagini dintr-un roman pe care editorii l-au primit cu laude și încurajări. Et Après, publicat în 2004, s-a vândut într-un milion de exemplare în Franța, a fost tradus în 23 de limbi și a ajuns pe peliculă cu John Malkovich și Evangeline Lilly în rolurile principale. Au urmat alte 12 romane de succes, dintre care două au fost adaptate pentru marele ecran.")
                        .pictureUrl("https://www.libris.ro/images/poze-autori/guillaumemusso.jpg")
                        .build(),
                Author.builder()
                        .name("Giovanni Arpino")
                        .description("Scriitorul italian GIOVANNI ARPINO s-a născut în 1927 în oraşul Pola, aflat în prezent pe teritoriul Croaţiei. În 1943, tatăl, ofiţer de carieră, trece în rezervă, iar familia Arpino se stabileşte în oraşul natal al mamei, la Bra, în Piemont. În 1953, după studii universitare la Torino şi îndeplinirea stagiului militar, Giovanni Arpino se căsătoreşte; cuplul se instalează la Torino, în ale cărui cercuri intelectuale tânărul deja îşi crease un nume, legând prietenii de-o viaţă cu mari figuri ale lumii literare italiene, între care Italo Calvino. Debutează în 1952, cu volumul Sei stato felice, Giovanni, urmat în 1957 de Il prezzo dell'oro (versuri). În 1960, apare romanul Un delitto d'onore, care va sta la baza faimoasei comedii a lui Pietro Germi Divorţ în stil italian (1961), cu Marcello Mastroianni în rolul principal. În 1964, Arpino primeşte prestigiosul premiu Strega pentru romanul L'ombra delle colline. Seria distincţiilor literare continuă, cu premiul Moretti în 1969, care recompensează romanul Parfum de femeie (Il buio e il miele), premiul Campiello în 1972, pentru Randagio è l'eroe şi cu Super Campiello în 1980, pentru Il fratello italiano. După o viaţă dedicată literaturii şi jurnalismului, Giovanni Arpino încetează din viaţă în 1987, la Torino.")
                        .pictureUrl("https://www.avvenire.it/c/2017/PublishingImages/bcff11316ee2400ea4b279d2a7d672f7/arpino1.jpg?width=1024")
                        .build(),
                Author.builder()
                        .name("M.C.Tudose")
                        .description("Maria Cristiana Tudose este o tânără autoare născută la Ploiești. A început să scrie din dorința de a-și vindeca sufletul, ocupație ce a devenit pasiune și remediu. După șase ani, pagina ei www.eusuntfemeie.com a reușit să ajungă la inimile a sute de mii de femei din întreaga lume. Prima ei carte Eu sunt femeie a devenit bestseller în România și Republica Moldova cu peste 40.000 de exemplare vândute. Este licențiată în Științe Politice la Istituto Cesare Alfieri din Florența.")
                        .pictureUrl("https://images.gr-assets.com/authors/1562777261p5/15715564.jpg")
                        .build(),
                Author.builder()
                        .name("Mihail Drumeș")
                        .description("Mihail Drumeș (pseudonim pentru Mihail V. Dumitrescu; n. 26 noiembrie 1901, Ohrida, Imperiul Otoman – d. 7 februarie 1982, București, RS România) a fost un dramaturg, nuvelist, realizator de proză scurtă și romancier român, foarte popular in perioada interbelică.")
                        .pictureUrl("https://cuvintecelebre.ro/wp-content/uploads/2014/10/Citat-de-Mihail-Drumes-346x188.png")
                        .build(),
                Author.builder()
                        .name("Mircea Eliade")
                        .description("Mircea Eliade (n. 24 februarie/9 martie 1907, București, Regatul României[2] – d. 22 aprilie 1986,[3][4][5][6][7][8][9][10][11][12][13] Chicago, Comitatul Cook, SUA[14])[15] a fost istoric al religiilor, scriitor de ficțiune, filosof și profesor de origine română la Universitatea din Chicago, din 1957, titular al catedrei de istoria religiilor Sewell L. Avery din 1962, naturalizat cetățean american în 1966, onorat cu titlul de Distinguished Service Professor. Autor a 30 de volume științifice, opere literare și eseuri filozofice traduse în 18 limbi și a circa 1200 de articole și recenzii cu o tematică variată. Opera completă a lui Mircea Eliade ar ocupa peste 80 de volume, fără a lua în calcul jurnalele sale intime și manuscrisele inedite. Este membru post-mortem al Academiei Române (din 1990).[16] ")
                        .pictureUrl("https://www.stelian-tanase.ro/wp-content/uploads/2014/02/eliade-1-200x200.jpg")
                        .build()
        );

        List<Story> stories = List.of(Story.builder()
                        .title("O dorință de Crăciun")
                        .category("Sărbători")
                        .description("Stephanie mai aruncă o privire spre ceas, să fie sigură că nu rămâne în urmă cu programul pe care singură şi-l impusese: 5:50 a.m. Astăzi, uşile magazinului se deschideau la ora şapte fix, nici mai devreme, nici mai târziu era cea mai aglomerată zi din an la Snow Zone, magazinul cu echipamente pentru schi din Maximum Glide, unde Stephanie lucra ca manager de aproape doi ani. Cu o oră înainte de deschiderea magazinului, Stephanie ajusta volumul boxelor stereo, ascunse departe de privirile clienţi...")
                        .link("https://cartigratis.com/files/pdf/descarca-fern-michaels-si-cathy-lamb-o-dorinta-de-craciun.pdf")
                        .pictureUrl("https://cartigratis.com/files/1473788635%7B__%7Dodorintadecraciuni.jpg")
                        .author(authors.get(0))
                        .build(),
                Story.builder()
                        .title("Maidanul cu dragoste")
                        .category("Romantism")
                        .description("Destinul meu începe mizer într-o mahala cu nume ruşinos.Închid ochii şi revăd decorul, ca un desen în cărbune peste care a trecut, apăsat, mina unui copil nebunatic.Eram o gânganie cu picioarele goale prin praf. Murise un flăcău în mahala şi-mi «dese măsa lui» pantaloni de pomană – pantaloni scumpi, prea lungi ca să nu-i răsfrâng de câteva ori pe glezne şi largi. Trebuia să-i leg strâns, cu un brăcinar de sfoară, să nu-mi cadă în vine. Purtam o cămașă desfăcută la gât, cu desene şi flori...")
                        .link("https://cartigratis.com/files/pdf/descarca-gm-zamfirescu-maidanul-cu-dragoste.pdf")
                        .pictureUrl("https://cartigratis.com/files/1474555270%7B__%7Dmaidanul-cu-dragoste.jpg")
                        .author(authors.get(1))
                        .build(),
                Story.builder()
                        .title("Femeia de ciocolată")
                        .category("Romantism")
                        .description("Prăpastia aceea înfiorătoare avea pentru Negrişor ceva magnetic. De câte ori o vizita pe domnişoaraEleonora, simţea parcă o nevoie stranie să se apropie de fereastră şi să-şi lase jumătatea superioară acorpului deasupra abisului. Atunci un drăcuşor începea să-l gâdile sub tălpi şi-l făcea să salte când peuna, când pe alta. Uneori se ţinea numai într-un deget de picior, iar mâinile împreunate îi cădeau de-alungul zidului, de parcă ar fi fost gata să se arunce înot.Numai domnişoara Eleonora putea...")
                        .link("https://cartigratis.com/files/pdf/descarca-gib-mihaescu-femeia-de-ciocolata.pdf")
                        .pictureUrl("https://cartigratis.com/files/1475852789%7B__%7Dfemeia-de-ciocolata.jpg")
                        .author(authors.get(2))
                        .build(),
                Story.builder()
                        .title("Parfum de femeie")
                        .category("Romantism")
                        .description("Un muscoi auriu bîzîia de-a lungul ferestrei de pe palier, pereţii miroseau a vopseaproaspătă, cu un viraj neaşteptat muscoiul dibui din fericire aerul şi identifică spaţiul dintregeamurile întredeschise, dispăru. M-am dus şi eu la geam ca să arunc mucul de ţigară.Curtea de jos era goală, două palme sterpe de ciment în soarele de sfîrşit de august. Îndepărtare, verdele uzat al colinelor de peste rîu fumega sub un cer opac. Mi-am controlat cumîinile boneta bine pusă pe frunte, nodul şi linia core... ")
                        .link("https://cartigratis.com/files/pdf/descarca-giovanni-arpino-parfum-de-femeie.pdf")
                        .pictureUrl("https://cartigratis.com/files/1476007075%7B__%7Dparfum-de-femeie.jpg")
                        .author(authors.get(3))
                        .build(),
                Story.builder()
                        .title("Candidații la fericire")
                        .category("Contemporan")
                        .description("Nu ţi-am scris niciodată, aşa că-nţelegi cât de greu îmi este tocmai acum – când îţi relatez onmormântarela care suntem participanţi şi «sărbătoriţi» în acelaşi timp. Nici nu ţi-aş fi spus ceea ce-aisă vezi aici dacă nu mi-ai fi cerut explicaţii. Mă-ntrebi, din când în când, de ce m-am schimbat, cuaceeaşiîncăpăţânare cu care se discută la-nmormântări de ce-a murit răposatul, ca şi când n-ar fi acelaşilucru că a murit de cancer, de pneumonie sau călcat de-o maşină. Nu suntem obişnuiţi să acceptăm... ")
                        .link("https://cartigratis.com/files/pdf/descarca-ileana-vulpescu-candidatii-la-fericire.pdf")
                        .pictureUrl("https://cartigratis.com/files/1477720034%7B__%7DIleana-Vulpescu---Candidatii-La-Fericire.jpg")
                        .author(authors.get(4))
                        .build(),
                Story.builder()
                        .title("Arta conversației")
                        .category("Psihologie")
                        .description("Prin vinete-amurguri, prin veştede ramuri, se-apropie toamna cu paşiarămii... Din vămile văzduhului toamna pornise tiptil printre frunzemoarte, printre vrejuri şi printre crengi trosnitoare, vulpe tăcutămăturînd pămîntul cu coada, păşind cu sunet înfundat de labeprudente, scrutînd orizontul cu ochi micşoraţi şi reflexivi, prospectîndatent împrejurimile ca un strateg încercat.Cu o săptămînă mai-nainte, pe Transfăgărăşan, din verdele-mbătrînitşi din rugină, din curcubeul de culori şi de transparen... ")
                        .link("https://cartigratis.com/files/pdf/descarca-ileana-vulpescu-arta-conversatiei.pdf")
                        .pictureUrl("https://cartigratis.com/files/1477720293%7B__%7DIleana-Vulpescu-Arta-compromisului.jpg")
                        .author(authors.get(4))
                        .build(),
                Story.builder()
                        .title("De-amor, de-amar, de inimă albastră")
                        .category("Romantism")
                        .description("Timpul timp şi este şi un timp al fiecăruia. Ultimul are şi început şi sfîrşit. Pentru om - măcaraşa- ne-nchipuim, adică doar pentru om - există o singură certitudine: sfîrşitul. Toate fiinţeleşisimt şi îşi presimt sfîrşitul: doar că nu-l cunosc de la-nceputul vieţii. Sau măcar asta credemnoi oamenii. Oricît te-ai considera de pregătit pentru el, nu eşti. Fiindcă altminteri ar trebuisă-l priveşti ca traversatul de pe un trotuar pe cel de vizavi. Pe măsură ce îmbătrîneşti îţi vinîn minte pe negîn...   ")
                        .link("https://cartigratis.com/files/pdf/descarca-ileana-vulpescu-de-amor-de-amar-de-inima-albastra.pdf")
                        .pictureUrl("https://cartigratis.com/files/1477720832%7B__%7Dde-inima-albastra.jpg")
                        .author(authors.get(4))
                        .build(),
                Story.builder()
                        .title("Invitație la vals")
                        .category("Romantism")
                        .description("Totul s-a sfîrşit: nu-mi rămîne decît să mă sinucid. Sînt ferm convins că orice aş face de aici înainte nu rezolv nimic. Aşa că, neavînd încotro, mă văd silit să-mi plec steagul în faţa morţii. Sinuciderea se dovedeşte a fi un imperativ peste înţelegerea şi voinţa mea, un imperativ aş zice organic ― împotriva căruia orice rezistenţă pare lipsită de sens, ridicolă chiar. Nu exagerez deloc; în mine e viu numai un singur gînd: acela de a muri. În faţa lui, celelalte gînduri au amuţit, paralizate de")
                        .link("https://cartigratis.com/files/pdf/descarca-mihail-drumes-invitatie-la-vals.pdf")
                        .pictureUrl("https://cartigratis.com/files/1501821274%7B__%7Dinvitatia-la-vals.jpg")
                        .author(authors.get(5))
                        .build(),
                Story.builder()
                        .title("Maitreyi")
                        .category("Dramă")
                        .description("Am şovăit atîta în faţa acestui caiet, pentru că n-am izbutit să aflu încă ziua precisă cînd am întîlnit-o pe Maitreyi. În însemnările mele din acel an n-am găsit nimic. Numele ei apare acolo mult mai tîrziu, după ce am ieşit din sanatoriu şi a trebuit să mă mut în casa inginerului Narendra Sen, în cartierul Bhowanipore. Dar aceasta s-a întîmplat în 1929, iar eu întîlnisem pe Maitreyi cu cel puţin zece luni mai înainte. Şi dacă sufăr oarecum începînd această povestire, e tocmai pentru că nu ştiu...")
                        .link("https://cartigratis.com/files/pdf/descarca-mircea-eliade-maitreyi.pdf")
                        .pictureUrl("https://cartigratis.com/files/1502080244%7B__%7Dmaitreyi-eliade.jpg")
                        .author(authors.get(6))
                        .build());

        authorRepository.saveAll(authors);
        storyRepository.saveAll(stories);
    }
}

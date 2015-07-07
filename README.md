# Twitter Sentiment Analysis

##O projektu

[Twitter] (https://twitter.com/) je društvena mreža koja korisnicima omogućava da objavljuju i čitaju kratke poruke koji se nazivaju tweet-ovi i koji izražavaju njihovo mišljenje o različitim aspektima života. Osnovna ideja ovog projekta je kreiranje aplikacije sposobne da, na osnovu prikupljene kolekcije statusa, odnosno tweet-ova, nauči da prepozna razliku između statusa sa pozitivnim i negativnim osećanjem i da nakon toga uspešno klasifikuje tweet-ove koje joj korisnik zadaje. Trening, odnosno učenje aplikacije se postiže pomoću nenadgledanih metoda mašinskog učenja.
U ovom radu izvršena je klasifikacija tweet-ova isključivo na srpskom jeziku, što je i osnovna razlika u odnosu na projekat pod nazivom [Twitter Sentiment Classification using Distant Supervision] (http://s3.eddieoz.com/docs/sentiment_analysis/Twitter_Sentiment_Classification_using_Distant_Supervision.pdf), na kojem je rad zasnovan. 

##Rešenje

Tok izvršenja aplikacije se sastoji iz sledećih koraka:

1.	Prikupljanje statusa i njihovo skladištenje

2.	Procesiranje prethodno sačuvanih podataka

3.	Kreiranje skupa podataka za trening

4.	Primena metoda mašinskog učenja


###Prikupljanje statusa i njihovo skladištenje

Za prikupljanje tweet-ova korišćen je [Twitter Search API] (https://dev.twitter.com/rest/public/search), deo Twitter-ovog **REST API**-ja. On za zadati upit sa definisanim parametrima vraća statuse koji odgovaraju tim parametrima. Pre korišćenja ovog API-ja neophodno je registrovati aplikaciju na Twitter-u.
 
Potrebno je napisati upit koji će eliminisati tweet-ove koje predstavljaju retweet-ove (status koji je određeni korisnik preuzeo od nekog drugog korisnika), a za kriterijum pretrage prvo je postavljen emotikon :) za dobijanje statusa koji izražavaju pozitivna osećanja a zatim emotikon :(, za dobijanje tweet-ova koji će kasnije biti označeni kao negativni. Dobijeni upiti su:
```
:) + exclude:retweets

:( + exclude:retweets
```

U narednoj tabeli su dati emotikoni koje twitter mapira na :) i emotikoni koje twitter mapira na :( :


| Mapirani na :)  | Mapirani na :( |
| ------------- | ------------- |
| :)  | :(  |
| :-)  | :-(  |
| : )  | : (  |
| :D  |   |
| =)  |   |

Kako postoji ograničenje na broj statusa koje klijentska aplikacija može da dobije u jednom zahtevu (najviše 100), neophodna je iteracija kroz rezultat pretrage.

Da se u rezultatu ne bi javljali duplikati, koristi se parametar **max_id**. U prvom zahtevu zadaje se samo broj statusa, a za sledeće se beleži najmanji ID od svih primljenih tweet-ova. Taj ID se umanjuje za jedan i prosleđuje kao vrednost za **max_id** narednog zahteva. Tako se obezbeđuje da on vraća samo statuse sa ID-ijem koji je manji ili jednak vrednosti **max_id** parametra.

Za upit se postavlja još jedan parametar koji se odnosi na jezik na kojem će biti statusi koji su rezultat pretrage. Taj parametar je **lang** i dodeljuje mu se vrednost **sr** kako bi upit vratio samo tweet-ove na srpskom jeziku. 

U radu je potrebno 1000 negativnih i 1000 pozitivnih statusa, pa se ovaj postupak ponavlja po 10 puta za oba slučaja. Za ovaj rad, bilo je potrebno oko dva meseca za prikupljanje ovih podataka.

Prikupljeni statusi se skladište u json fajlovima [data/positive.json] (https://raw.githubusercontent.com/DunjaKlisic/TwitterSentimentAnalysis/master/data/positive.json) i [data/negative.json](https://raw.githubusercontent.com/DunjaKlisic/TwitterSentimentAnalysis/master/data/negative.json).

###Procesiranje prethodno sačuvanih podataka

Prvi korak u obradi podataka je njihovo učitavanje.

Nakon toga, uklanjaju se emotikoni jer bi kreirali šum prilikom treniranja klasifikatora. Za pronalaženje emotikona u ovom radu su korišćeni regularni izrazi koji u ovom slučaju ima sledeći oblik:
```
[:;=B](')*(-)*[)(P3DSO*sp]+
```
Sledeći korak u obradi teksta statusa je dodavanje tokena USERNAME i URL koji predstavljaju korisnička imena i linkove. Regularni izrazi koji se koriste za pronalaženje korisničkih imena i linkova su, respektivno:
```
@\\w*

http(s)*://t.co/\\w*
```
Poslednja stvar u procesiranju teksta je pronalaženje reči u kojima postoje slova koja se ponavljaju više od dva puta zaredom i njihova zamena sa dva pojavljivanja tog slova. Ovo se postiže sledećim izrazom:
```
([a-zA-Zа-шА-Ш])\\1+
```
###Kreiranje skupa podataka za trening

Za kreiranje skupa podataka za trening se koristi tekst koji je dobijen kao rezultat prethodnog koraka. On predstavlja prvi atribut. Drugi atribut predstavlja osećanje izraženo u statusu, odnosno tekstu statusa i može uzeti dve vrednosti: **positive** ili **negative**.  To je atribut koji predstavlja klasu. Dobijeni skup podataka se čuva u [arff](http://www.cs.waikato.ac.nz/ml/weka/arff.html) fajlu [data/trainingDataSet.arff](https://github.com/DunjaKlisic/TwitterSentimentAnalysis/blob/master/data/trainingDataSet.arff). Ovi fajlovi predstavljaju tekstualne fajlove koji opisuju listu instanci koje dele skup atributa.

###Primena metoda mašinskog učenja

Korišćena su tri različita klasifikatora: **Naive Bayes**, **Maximum Entropy** i **Support Vector Machines**. Trening se izvršava nad skupom podataka dobijenim u prethodnom koraku i za dve varijante, kada se koriste unigrami ili bigrami. Na osnovu ovog treninga, klasifikator treba da bude u stanju da pravilno odredi da li je u statusu koji mu je prosleđen iskazano pozitivno ili negativno osećanje.

##Tehnička realizacija

U ovom odeljku biće opisan način na koji su realizovani gore navedeni koraci. Korišćen je **Java** programski jezik.

###Prikupljanje statusa i njihovo skladištenje

Koristi se [twitter4j](http://twitter4j.org/en/index.html) biblioteka za Javu. To je nezvanična **Java** biblioteka za [Twitter API](https://dev.twitter.com/overview/documentation) koja obezbeđuje jednostavan pristup Twitter-ovim servisima iz **Java** aplikacija. 

Nakon registracije  aplikacije dobijaju se podaci koje je potrebno uključiti u [twitter4j.properties](https://github.com/DunjaKlisic/TwitterSentimentAnalysis/blob/master/config/twitter4j.properties) fajl koji treba da se nalazi na classpath-u projekta. Obavezan sadržaj ovog fajla dat je nastavku:
```
debug=true

oauth.consumerKey=*********************

oauth.consumerSecret=******************************************

oauth.accessToken=**************************************************

oauth.accessTokenSecret=******************************************
```
Za postavljanje upita koji će vratiti tweet-ove sa pozitivnim sentimentom koristi se klasa **Query** biblioteke [twitter4j](http://twitter4j.org/en/index.html). 
```
Query query = new Query();

query.setLang("sr");

query.setQuery(":)+exclude:retweets");

query.setMaxId(lastID-1);
```
Metoda **setLang("sr")** postavlja parametar koji određuje na kom jeziku će biti statusi iz rezultata upita. Postavljen je na **sr** jer se vrši klasifikacija tweet-ova na srpskom jeziku.
Metodom  **setQuery(":)+exclude:retweets")** postavlja se parametar pretrage i iz rezultata isključuju statusi koji predstavljaju retweet-ove.
Na kraju, metodom **setMaxId(lastID-1)** zadaje se vrednost parametru maxId.
Postupak prikupljanja statusa koji izražavaju negativna osećanja je analogan opisanom postuku.

Za čuvanje podataka korišćena je [gson](https://code.google.com/p/google-gson/) biblioteka i njena metoda **toJson()** kojoj se prosleđuje objekat koji je potrebno sačuvati u json formatu. Statusi prikupljeni na ovaj način se nalaze u fajlovima [data/positive.json](https://raw.githubusercontent.com/DunjaKlisic/TwitterSentimentAnalysis/master/data/positive.json) i [data/negative.json](https://raw.githubusercontent.com/DunjaKlisic/TwitterSentimentAnalysis/master/data/negative.json). Primer jednog statusa u json formatu je:

```
{

"createdAt":"Jul 3, 2015 8:51:23 PM",

"id":617042974875197441,

"text":"@villmabg А шта ћемо ми с нашом кризом? :(",

"lang":"sr",

...

}
```
###Procesiranje prethodno sačuvanih podataka

Najpre se prethodni podaci učitavaju koristeći metodu **createStatus()** klase **TwitterObjectFactory** koja kao parametr prima json objekat i na osnovu njega kreira instancu klase **Status**. 

Za obradu statusa, odnosno njihovog teksta, koristi se metoda **replaceAll()** klase **String**. Ona kao prvi parametar prima regularni izraz koji se traži (koji su navedeni u prethodnom odeljku), a kao drugi tekst kojim se zameni struktura koja se poklopi sa konkretnim regularnim izrazom. U slučaju emotikona, oni se zamenjuju praznim String-ovima, korisnička imena tagom USERNAME, a linkovi tagom URL. Slova koja se ponavljaju više od dva puta zaredom menjaju se sa dva pojavljivanja istog slova. Primer korišćenja ove metode je:
```
String noUsernames=tweets.get(i).replaceAll("@\\w*", "USERNAME ");
```
###Kreiranje skupa podataka za trening

Za kreiranje skupa podataka za trening i primenu metoda mašinskog učenja korišćena je biblioteka [weka](http://www.cs.waikato.ac.nz/ml/weka/). Ona predstavlja kolekciju algoritama za mašinsko učenje koji se koriste za data mining. To je softver otvorenog koda koji se može primeniti direktno na skup podataka ili pozvati iz **Java** koda.

Najpre se kreiraju atributi skupa podataka za trening predstavljane klasom **Attribute**, procesirani tekst kao prvi atribut, a izraženi sentiment kao drugi atribut koji može da uzme dve vrednost – **positive** i **negative**.

Nakon toga, za svaki status se kreira instanca, odnosno jedan red dataset-a pomoću klase **Instance** i svakoj od njih za prvi atribut se postavlja tekst statusa, a za drugi odgovarajući sentiment.

Poslednji korak je kreiranje dataset-a (klasa **Instances**) i ubacivanje svih prethodno napravljenih instanci.

###Primena metoda mašinskog učenja


Najpre se učitava dataset pomoću klase **DataSource** kojoj se u konstruktoru prosleđuje putanja ka **arff** fajlu i njegove metode **getDataSet()**.	Metodi **setClassIndex(int index)** se prosleđuje parameter 1 kako bi se označilo da drugi atribut predstavlja klasu jedne instance.  

Zatim se kreira instanca klase **NGramTokenizer**. Pomoću njene metode **setNGramMaxSize(nGramMaxSize)** bira se da li se radi sa unigramima ili bigramima.

Objektu klase **StringToWordVector** se prosleđuje prethodno učitani skup podataka i kreirani tokenizer.

Kako bi ostali samo atributi koji su značajni za analizu, koristi se **AttibuteSelectionFilter**, a zatim se kreira **MultiFilter** koji obuhvata ovaj filter i **StringToWordVector** filter.

Zatim se kreira klasifikator sa filterima kojem se prosleđuju ovi filteri i željeni klasifikator (klase **NaiveBayes**, **J48** (Maximum Entropy) ili **LibSVM** (Support Vector Machines)). Trening se vrši pomoću metode **buildClassifier(trainingDataSet)**. Za klasifikaciju nekog konkretnog statusa koristi se metoda **classifyInstance(Instance status)**.	

##Analiza

Preciznost predstavlja procenat statusa koji su stvarno pozitivni od svih statusa koji su označeni kao pozitivni. Odziv je procenat statusa koji su označeni kao pozitivni, od ukupno pozitivnih statusa. U nastavku su date vrednosti za preciznost i odziv, po klasifikatorima, za unigrame i bigrame, respektivno:


| klasifikator  | preciznost | odziv |
| --- | --- | ------------- |
| Naive Bayes  | 0,582  | 0,745  |
| Maximum Entrophy  | 0,582 | 0,745  |
| Support Vector Machines  | 0,582 | 0,745  |



| klasifikator  | preciznost | odziv |
| --- | --- | ------------- |
| Naive Bayes  | 0,642  | 0,178  |
| Maximum Entrophy  | 0,642 | 0,178  |
| Support Vector Machines  | 0,642 | 0,178 |

Nakon treniranja klasifikatora različitim metodama mašinskog učenja, uočili smo da sva tri pristupa daju iste rezultate. Preciznost je veća kada se koriste bigrami, za razliku od odziva koji je bolji kada se koriste unigrami. Kada se koriste unigrami, procenat pravilno klasifikovanih statusa je 60,5% ili 1210 od 2000 instanci. Što se tiče bigrama, ovaj procenat opada na 53,95%.

U prvom slučaju, najveća greška se javlja kod 535 negativnih tweet-ova koji su klasifikovani kao pozitivni nasuprot 255 pogrešno klasifikovanih pozitivnih statusa. U drugom slučaju, čak 822 pozitivna statusa su klasifikovana kao negativni.

Klasifikatorima je prosleđen i status “Ovaj dan se nikada neće završiti :(“ koji su svi ispravno klasifikovali kao negativan.

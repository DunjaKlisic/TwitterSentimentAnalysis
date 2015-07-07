# Twitter Sentiment Analysis

##O projektu

[Twitter] (https://twitter.com/) je društvena mreža koja korisnicima omogućava da objavljuju i čitaju kratke poruke koji se nazivaju tweet-ovi i koji izražavaju njihovo mišljenje o različitim aspektima života. Osnovna ideja ovog projekta je kreiranje aplikacije sposobne da, na osnovu prikupljene kolekcije statusa, odnosno tweet-ova, nauči da prepozna razliku između statusa sa pozitivnim i negativnim osećanjem i na nakon toga uspešno klasifikuje tweet-ove koje joj korisnik zadaje. Trening, odnosno učenje aplikacije se postiže pomoću nenadgledanih metoda mašinskog učenja.
U ovom radu izvršena je klasifikacija tweet-ova isključivo na srpskom jeziku, što je i osnovna razlika u odnosu na projekat pod nazivom [Twitter Sentiment Classification using Distant Supervision] (http://s3.eddieoz.com/docs/sentiment_analysis/Twitter_Sentiment_Classification_using_Distant_Supervision.pdf), na kojem je rad zasnovan. 

##Rešenje

Tok izvršenja aplikacije se sastoji iz sledećih koraka:

1.	Prikupljanje statusa i njihovo skladištenje

2.	Procesiranje prethodno sačuvanih podataka

3.	Kreiranje skupa podataka za trening

4.	Primena metoda mašinskog učenja


###Prikupljanje statusa i njihovo skladištenje

Za prikupljanje tweet-ova korišćen je [Twitter Search API] (https://dev.twitter.com/rest/public/search), deo Twitter-ovog **REST API**-ja. On za zadati upit sa definisanim parametrima vraća statuse koji odgovaraju tim parametrima. Pre korišćenja ovog API-ja neophodno je registrovati aplikaciju na Twitter-u.
 
Potreban nam je upit koji će eliminisati tweet-ove koje predstavljaju retweet-ove (status koji je određeni korisnik preuzeo od nekog drugog korisnika), a za kriterijum pretrage prvo je postavljen emotikon :) za dobijanje statusa koji izražavaju pozitivna osećanja a zatim emotikon :(, za dobijanje tweet-ova koje ćemo kasnije označiti kao negativne. Dobijeni upiti su:

:) + exclude:retweets

:( + exclude:retweets

U narednoj tabeli su dati emotikoni koje twitter mapira na :) i emotikoni koje twitter mapira na :( :


| Mapirani na :)  | Mapirani na :( |
| ------------- | ------------- |
| :)  | :(  |
| :-)  | :-(  |
| : )  | : (  |
| :D  |   |
| =)  |   |

Kako postoji ograničenje na broj statusa koje klijentska aplikacija može da dobije u jednom zahtevu (najviše 100), neophodno je iteracija kroz rezultat pretrage.

Da se u rezultatu ne bi javljali duplikati, koristi se parametar **max_id**. U prvom zahtevu zadaje se samo broj statusa, a za sledeće se beleži najmanji ID od svih primljenih tweet-ova. Taj ID se umanjuje za jedan i prosleđuje kao vrednost za **max_id** narednog zahteva. Tako se obezbeđuje da on vraća samo statuse sa ID-ijem koji je manji ili jednak vrednosti **max_id** parametra.

Za upit se postavlja još jedan parametar koji se odnosi na jezik na kojem će biti statusi koji su rezultat pretrage. Taj parametar je **lang** i dodeljuje mu se vrednost **sr** kako bi upit vratio samo tweet-ove na srpskom jeziku. 

U radu nam je potrebno 1000 negativnih i 1000 pozitivnih statusa, pa ovaj postupak ponavljamo po 10 puta za oba slučaja.

Prikupljenei statusi se skladište u json fajlu LINK.

###Procesiranje prethodno sačuvanih podataka

Prvi korak u obradi podataka je njihovo učitavanje.

Nakon toga, uklanjaju se emotikoni jer bi kreirali šum prilikom treniranja klasifikatora. Za pronalaženje emotikona u ovom radu su korišćeni regularni izrazi koji u ovom slučaju ima sledeći oblik:

[:;=B](')*(-)*[)(P3DSO*sp]+

Sledeći korak u obradi teksta statusa je dodavanje tokena USERNAME i URL koji predstavljaju korisnička imena i linkove. Regularni izrazi koji se koriste za pronalaženje korisničkih imena i linkova su, respektivno:

@\\w*

http(s)*://t.co/\\w*

Poslednja stvar u procesiranju teksta je pronalaženje reči u kojima postoje slova koja se ponavljaju više od dva puta zaredom i njihova zamena sa dva pojavljivanja tog slova. Ovo se postiže sledećim izrazom:

([a-zA-Zа-шА-Ш])\\1+

###Kreiranje skupa podataka za trening

Za kreiranje skupa podataka za trening se koristi tekst koji je dobijen kao rezultat prethodnog koraka. On predstavlja prvi atribut. Drugi atribut predstavlja osećanje izraženo u statusu, odnosno tekstu statusa i može uzeti dve vrednosti: **positive** ili **negative**.  To je atribut koji predstavlja klasu. Dobijeni skup podataka se čuva u **arff** fajlu. 

###Primena metoda mašinskog učenja

Koristimo tri različita klasifikatora: **Naive Bayes**, **Maximum Entropy** i **Support Vector Machines**. Trening se izvršava nad skupom podataka dobijenim u prethodnom koraku i za dve varijante, kada se koriste unigrami ili bigrami. Na osnovu ovog treninga, klasifikator treba da bude u stanju da pravilno odredi da li je u statusu koji mu je prosleđen iskazano pozitivno ili negativno osećanje.

##Tehnička realizacija

U ovom odeljku biće opisan način na koji su realizovani gore navedeni koraci. Korišćen je **Java** programski jezik.

###Prikupljanje statusa i njihovo skladištenje

Koristi se **twitter4j** biblioteka za Javu. Nakon registracije  aplikacije dobijaju se podaci koje je potrebno uključiti u twitter4j.properties fajl koji treba da se nalazi na classpath-u projekta. Obavezan sadržaj ovog fajla dat je nastavku:

debug=true

oauth.consumerKey=*********************

oauth.consumerSecret=******************************************

oauth.accessToken=**************************************************

oauth.accessTokenSecret=******************************************

Za postavljanje upita koji će vratiti tweet-ove sa pozitivnim sentimentom koristi se klasa **Query** biblioteke **twitter4j**. 

Query query = new Query();

query.setLang("sr");

query.setQuery(":)+exclude:retweets");

query.setMaxId(lastID-1);

Metoda **setLang("sr")** postavlja parametar koji određuje na kom jeziku će biti statusi iz rezultata upita. Postavljen je na sr jer se vrši klasifikacija tweet-ova na srpskom jeziku.
Metodom  **setQuery(":)+exclude:retweets")** postavlja se parametar pretrage i iz rezultata isključuju statusi koji predstavljaju retweet-ove.
Na kraju, metodom **setMaxId(lastID-1)** zadaje se vrednost parametru maxId.
Postupak prikupljanja statusa koji izražavaju negativna osećanja je analogan opisanom postuku.

Za čuvanje podataka korišćena je **gson** biblioteka i njena metoda **toJson()** kojoj se prosleđuje objekat koji je potrebno sačuvati u json formatu. Statusi prikupljeni na ovaj način se nalaze u fajlovima **positive.json** i **negative.json**. Primer jednog statusa u json formatu je:


{

"createdAt":"Jul 3, 2015 8:51:23 PM",

"id":617042974875197441,

"text":"@villmabg А шта ћемо ми с нашом кризом? :(",

"source":"\u003ca href\u003d\"http://twitter.com\" rel\u003d\"nofollow\"\u003eTwitter Web Client\u003c/a\u003e",

"isTruncated":false,

"inReplyToStatusId":617042233108398080,

"inReplyToUserId":804997680,

"isFavorited":false,

"isRetweeted":false,

"favoriteCount":1,

"inReplyToScreenName":"villmabg",

"place":{

"name":"Sremski Karlovci",

"countryCode":"RS",

"id":"01d442d72752480c",

"country":"Srbija",

"placeType":"city",

"url":"https://api.twitter.com/1.1/geo/id/01d442d72752480c.json",

"fullName":"Sremski Karlovci, Srbija",

"boundingBoxType":"Polygon",

"boundingBoxCoordinates":[[

{"latitude":45.145548,"longitude":19.890133},

{"latitude":45.145548,"longitude":20.014673},

{"latitude":45.226132,"longitude":20.014673},

{"latitude":45.226132,"longitude":19.890133}]],

"containedWithIn":[]

},

"retweetCount":0,

"isPossiblySensitive":false,

"lang":"sr",

"contributorsIDs":[],

"userMentionEntities":[{

"name":"Frau K",

"screenName":"villmabg",

"id":804997680,

"start":0,

"end":9}],

"urlEntities":[],

"hashtagEntities":[],

"mediaEntities":[],

"extendedMediaEntities":[],

"symbolEntities":[],

"currentUserRetweetId":-1,

"user":{

"id":2955497123,

"name":"Богосављевићка",

"screenName":"HumanSkeleton_",

"location":"Sremski Karlovci, Serbia",

"description":"Više ne znam šta da pišem ovde, možda je bolje bez opisa, šta god da napišem ne ispadne nešto, tako da mu dođe na isto, pa neka onda bude ovako.",

"descriptionURLEntities":[],

"isContributorsEnabled":false,

"profileImageUrl":"http://pbs.twimg.com/profile_images/550757555551473664/1eC8UNsm_normal.jpeg",

"profileImageUrlHttps":"https://pbs.twimg.com/profile_images/550757555551473664/1eC8UNsm_normal.jpeg",

"isDefaultProfileImage":false,

"isProtected":false,

"followersCount":129,

"profileBackgroundColor":"C0DEED",

"profileTextColor":"333333",

"profileLinkColor":"0084B4",

"profileSidebarFillColor":"DDEEF6",

"profileSidebarBorderColor":"C0DEED",

"profileUseBackgroundImage":true,

"isDefaultProfile":true,

"showAllInlineMedia":false,

"friendsCount":168,

"createdAt":"Jan 1, 2015 9:55:12 PM",

"favouritesCount":268,

"utcOffset":7200,

"timeZone":"Belgrade",

"profileBackgroundImageUrl":"http://abs.twimg.com/images/themes/theme1/bg.png",

"profileBackgroundImageUrlHttps":"https://abs.twimg.com/images/themes/theme1/bg.png",

"profileBannerImageUrl":"https://pbs.twimg.com/profile_banners/2955497123/1424526346",

"profileBackgroundTiled":false,

"lang":"en",

"statusesCount":806,

"isGeoEnabled":true,

"isVerified":false,

"translator":false,

"listedCount":3,

"isFollowRequestSent":false

}

}

###Procesiranje prethodno sačuvanih podataka

Najpre se prethodni podaci učitavaju koristeći metodu **createStatus()** klase **TwitterObjectFactory** koja kao parametra prima json objekat i na osnovu njega kreira instancu klase **Status**. 

Za obradu statusa, odnosno njihovog teksta, koristi se metoda **replaceAll()** klase **String**. Ona kao prvi parametar prima regularni izraz koji se traži (koji su navedeni u prethodnom odeljku), a kao drugi tekst kojim zamenimo strukturu koja se poklopi sa konkretnim regularnim izrazom. U slučaju emotikona, zamenjuju se praznim String-ovima, korisnička imena tagom USERNAME, a linkove tagom URL. Slova koja se ponavljaju više od dva puta zaredom menjaju se sa dva pojavljivanja istog slova. Primer korišćenja ove metode je:

String noUsernames=tweets.get(i).replaceAll("@\\w*", "USERNAME ");

###Kreiranje skupa podataka za trening

Za kreiranje skupa podataka za trening i primenu metoda mašinskog učenja korišćena je biblioteka **weka**. 

Najpre se kreiraju atributi skupa podataka za trening predstavljane klasom **Attribute**, procesirani tekst kao prvi atribut, a izraženi sentiment kao drugi atribut koji može da uzme dve vrednost – **positive** i **negative**.

Nakon toga, za svaki status se kreira instanca, odnosno jedan red dataset-a pomoću klase **Instance** i svakoj od njih za prvi atribut se postavlja tekst statusa, a za drugi odgovarajući sentiment.

Poslednji korak je kreiranje dataset-a (klasa **Instances**) i ubacivanje svih prethodno napravljenih instanci.

###Primena metoda mašinskog učenja


Najpre se učitava dataset pomoću klase **DataSource** kojoj se u konstruktoru prosleđuje putanja ka **arff** fajlu i njegove metode **getDataSet()**.	Metodi **setClassIndex(int index)** se prosleđuje parameter 1 kako bi se označilo da drugi atribut predstavlja klasu jedne instance.  

Zatim se kreira instanca klase **NGramTokenizer**. Pomoću njene metode **setNGramMaxSize(nGramMaxSize)** bira se da li se radi sa unigramima ili bigramima.

Objektu klase **StringToWordVector** se proseđuje prethodno učitani skup podataka i kreirani tokenizer.

Kako bi ostali samo atributi koji su značajni za analizu, koristi se **AttibuteSelectionFilter**, a zatim se kreira **MultiFilter** koji obuhvata ovaj filter i **StringToWordVector** filter.

Zatim se kreira klasifikator sa filterima kojem se prosleđuju ovi filtere i željeni klasifikator (klase **NaiveBayes**, **J48** (Maximum Entropy) ili **LibSVM** (Support Vector Machines)). Trening se vrši pomoću metode **buildClassifier(trainingDataSet)**. Za klasifikaciju nekog konkretnog statusa koristi se metoda **classifyInstance(Instance status)**.	

##Analiza

Nakon treniranja klasifikatora sa različitim metodama mašinskog učenja, uočili smo da sva tri pristupa daju iste rezultate. Kada se koriste unigrami, procenat pravilno klasifikovanih statusa je 60,5% ili 1210 od 2000 instanci. Što se tiče bigrama, ovaj procenat opada na 53,95%. U nastavku su date matrice konfuzije za unigrame i bigrame, respektivno:


| a  | b | <--classified as |
| --- | --- | ------------- |
| 745  | 255  | a=positive  |
| 535  | 465 | b=negative  |



| a  | b | <--classified as |
| --- | ---| -------------- |
| 178  | 822  | a=positive  |
| 99  | 901 | b=negative  |



U prvom slučaju, najveća greška se javlja kod 535 negativnih tweet-ova koji su klasifikovani kao pozitivni nasuprot 255 pogrešno klasifikovanih pozitivnih statusa. U drugom slučaju, čak 822 pozitivna statusa su klasifikovani kao negativni.

Klasifikatorima je prosleđen i status “Ovaj dan se nikada neće završiti :(“ koji su svi ispravno klasifikovali kao negativan.

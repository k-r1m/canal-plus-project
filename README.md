  ________.__                  _____              _____              .__               
 /  _____/|__|__  __ ____     /     \   ____     /     \   _______  _|__| ____   ______
/   \  ___|  \  \/ // __ \   /  \ /  \_/ __ \   /  \ /  \ /  _ \  \/ /  |/ __ \ /  ___/
\    \_\  \  |\   /\  ___/  /    Y    \  ___/  /    Y    (  <_> )   /|  \  ___/ \___ \ 
 \______  /__| \_/  \___  > \____|__  /\___  > \____|__  /\____/ \_/ |__|\___  >____  >
        \/              \/          \/     \/          \/                    \/     \/
		
		
Desription :

	Application listant les titres originaux depuis un fichier TSV (récupéré depuis imdb) en fonction du genre, dans ce cas, comedy.
	
Pré-requis :

	Rabbitmq installé et démarré en arrière-plan 

Execution :

	java -jar GiveMeMovies.jar "path_to_file"

Il est possible de lister tous les films d'un autre genre :

	java -jar GiveMeMovies.jar "path_to_file" action
	
Code source disponible dans canal-plus-project

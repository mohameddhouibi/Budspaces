package com.example.budspaces.Samples;

import com.example.budspaces.Models.User;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.UUID;

/*
 * Created by Anton Bevza on 1/13/17.
 */
abstract class FixturesData {

    static SecureRandom rnd = new SecureRandom();

    static ArrayList<User> users = new ArrayList<>();

    static ArrayList<String> avatars = new ArrayList<String>() {
        {
            add("https://pbs.twimg.com/profile_images/921115156196839429/NU1rwvfC_400x400.jpg"); // Oliver 1
            add("https://i.pinimg.com/originals/8d/8b/36/8d8b36127562d32e3684176dac3edbb8.jpg"); // Laurel 2
            add("https://pbs.twimg.com/profile_images/1216443753545601024/2qvgkQO__400x400.jpg"); // Sarah 3
            add("https://vignette.wikia.nocookie.net/new-arrowverse/images/7/74/Quentin_Lance.png/revision/latest?cb=20180218064913"); // Quenten Lance 4
            add("https://www.premiere.fr/sites/default/files/styles/scale_crop_1280x720/public/2018-04/arrow_felicity_0.jpg"); // Felicity 5
            add("https://static2.cbrimages.com/wordpress/wp-content/uploads/2018/11/Arrow-John-Diggle.jpg"); // John 6
            add("https://i.pinimg.com/originals/52/c9/a5/52c9a540131bbf997f5d29591365f868.jpg"); // Roy Harper 7
            add("https://mtv.mtvnimages.com/uri/mgid:ao:image:mtv.com:14679?quality=0.8&format=jpg"); // Thea Queen 8
            add("https://www.premiere.fr/sites/default/files/styles/scale_crop_1280x720/public/2018-04/arrow_malcolm.jpg"); // malcolm merlyn 9
            add("https://i.pinimg.com/originals/83/e0/f5/83e0f57bc3af9b4ef560d21065db82ec.jpg"); // Tommy Merlyn 10
            add("https://img-4.linternaute.com/TgBTbjLks-xJE1zZRvl4zdaeXfM=/600x/smart/64bd359230844b1e97bcf39a86fb45a3/ccmcms-linternaute/2320942.jpg"); // Ray Palmer 11
            add("https://static1.purebreak.com/articles/4/14/10/34/@/579667-arrow-saison-6-manu-bennett-deathstro-diapo-1.jpg"); // Deathstroke 12
            add("https://4.bp.blogspot.com/-wD3UydXKaVA/VRbrndoUtbI/AAAAAAAAhVw/4I82vuJ9xMM/s1600/ARROW.png"); // Deadshot 13
            add("https://i.pinimg.com/originals/60/bb/76/60bb7636edd8f0e4a9650801c252bcb8.jpg"); // Barry Allen 14
            add("https://vignette.wikia.nocookie.net/headhuntersholosuite/images/0/0c/Arrow_4x22_013.jpg/revision/latest/scale-to-width-down/340?cb=20160616143322"); // Curtis 15
            add("https://i.pinimg.com/originals/e9/d3/d8/e9d3d828bcf66f3713b63d3801a1ff47.jpg"); // Rene Ramirez 16
            add("https://images-na.ssl-images-amazon.com/images/I/51LawZ1GU4L._AC_.jpg"); // Dinah 17
            add("https://vignette.wikia.nocookie.net/arrow/images/a/a6/Moira_Queen.png/revision/latest?cb=20191118124954"); // Moira 18
            add("https://vignette.wikia.nocookie.net/arrow-fanon/images/b/b5/Damien_Darhk.png/revision/latest?cb=20170314104711"); // Damian Dark 19
            add("https://i4.ytimg.com/vi/iwgy5TX7kEk/maxresdefault.jpg"); // Ras Al Ghoul 20
            add("https://vignette.wikia.nocookie.net/arrow/images/3/3f/Talia_al_Ghul.png/revision/latest?cb=20191030230855"); // Talia Al ghoul 21
            add("https://www.premiere.fr/sites/default/files/styles/scale_crop_1280x720/public/2018-05/arrow-katrina-law.jpg"); // Nyssa Al Ghoul 22
            add("https://vignette.wikia.nocookie.net/arrow/images/4/42/Anatoly_Knyazev.png/revision/latest?cb=20180210225621"); // Anatoly 23
            add("https://vignette.wikia.nocookie.net/arrow/images/5/5e/Adrian_Chase%2C_hallucination.png/revision/latest?cb=20180617042657"); // Adrian Chase 24
            add("https://4.bp.blogspot.com/-TaVgvV4XtCA/XACqO7OT_YI/AAAAAAAAwvs/8gJvdVrXQm46PYWEvre8sxT1Ie5DIhDwgCLcBGAs/s1600/Arrow-7x07-Ricardo-Diaz.jpg"); // Ricardo Diaz
        }
    };

    static final ArrayList<String> groupChatImages = new ArrayList<String>() {
        {
            add("http://i.imgur.com/hRShCT3.png");
            add("http://i.imgur.com/zgTUcL3.png");
            add("http://i.imgur.com/mRqh5w1.png");
        }
    };

    static final ArrayList<String> groupChatTitles = new ArrayList<String>() {
        {
            add("Samuel, Michelle");
            add("Jordan, Jordan, Zoe");
            add("Julia, Angel, Kyle, Jordan");
        }
    };

    static final ArrayList<String> names = new ArrayList<String>() {
        {
            add("Oliver Queen");
            add("Laurel Lance");
            add("Sarah Lance");
            add("Quenten Lance");
            add("Felicity Smoak");
            add("John Diggle");
            add("Roy Harper");
            add("Thea Queen");
            add("Malcolm Merlyn");
            add("Tommy Merlyn");
            add("Ray Palmer");
            add("Deathstroke");
            add("Deadshot");
            add("Barry Allen");
            add("Curtis Holt");
            add("Rene Ramirez");
            add("Dinah Drake");
            add("Moira Queen");
            add("Damian Dark");
            add("Ras Al ghul");
            add("Talia Al ghul");
            add("Nyssa Al ghul");
            add("Anatoly Knyazev");
            add("Adrian Chase");
            add("Ricardo Diaz");
        }
    };

    static final ArrayList<String> emails = new ArrayList<String>() {
        {
            add("green.arrow@arrowverse.com");
            add("black_canary@arrowverse.com");
            add("white_canary@arrowverse.com");
            add("detective@arrowverse.com");
            add("overwatch@arrowverse.com");
            add("spartan@arrowverse.com");
            add("arsenal@arrowverse.com");
            add("speedy@arrowverse.com");
            add("dark_archer@arrowverse.com");
            add("tommy.merlyn@arrowverse.com");
            add("the_atom@arrowverse.com");
            add("deathstroke@arrowverse.com");
            add("deadshot@arrowverse.com");
            add("the_flash@arrowverse.com");
            add("mr.terrific@arrowverse.com");
            add("wild_dog@arrowverse.com");
            add("new_black_canary@arrowverse.com");
            add("moira.queen@arrowverse.com");
            add("black_knight@arrowverse.com");
            add("ras.al.ghul@arrowverse.com");
            add("the_demon@arrowverse.com");
            add("Nyssa.ag@arrowverse.com");
            add("kgbeast@arrowverse.com");
            add("prometheus@arrowverse.com");
            add("dragon@arrowverse.com");
        }
    };

    static final ArrayList<String> genders = new ArrayList<String>() {
        {
            add("Homme");
            add("Femme");
            add("Femme");
            add("Homme");
            add("Femme");
            add("Homme");
            add("Homme");
            add("Femme");
            add("Homme");
            add("Homme");
            add("Homme");
            add("Homme");
            add("Homme");
            add("Homme");
            add("Homme");
            add("Homme");
            add("Femme");
            add("Femme");
            add("Homme");
            add("Homme");
            add("Femme");
            add("Femme");
            add("Homme");
            add("Homme");
            add("Homme");
        }
    };

    static final ArrayList<String> messages = new ArrayList<String>() {
        {
            add("Hello!");
            add("This is my phone number - +1 (234) 567-89-01");
            add("Here is my e-mail - myemail@example.com");
            add("Hey! Check out this awesome link! www.github.com");
            add("Hello! No problem. I can today at 2 pm. And after we can go to the office.");
            add("At first, for some time, I was not able to answer him one word");
            add("At length one of them called out in a clear, polite, smooth dialect, not unlike in sound to the Italian");
            add("By the bye, Bob, said Hopkins");
            add("He made his passenger captain of one, with four of the men; and himself, his mate, and five more, went in the other; and they contrived their business very well, for they came up to the ship about midnight.");
            add("So saying he unbuckled his baldric with the bugle");
            add("Just then her head struck against the roof of the hall: in fact she was now more than nine feet high, and she at once took up the little golden key and hurried off to the garden door.");
        }
    };

    static final ArrayList<String> interests = new ArrayList<String>() {
        {
            add("web");
            add("ui");
            add("illustrator");
            add("unity");
            add("graphics");
            add("interface");
            add("adobe");
            add("fifa");
            add("games");
            add("musique");
            add("litterature");
            add("films");
            add("series");
            add("sorties");
            add("restaurants");
            add("promenades");
            add("aventures");
        }
    };

    static final ArrayList<String> images = new ArrayList<String>() {
        {
            add("https://habrastorage.org/getpro/habr/post_images/e4b/067/b17/e4b067b17a3e414083f7420351db272b.jpg");
            add("https://cdn.pixabay.com/photo/2017/12/25/17/48/waters-3038803_1280.jpg");
        }
    };

    static String getRandomId() {
        return Long.toString(UUID.randomUUID().getLeastSignificantBits());
    }

    static String getRandomAvatar() {
        return avatars.get(rnd.nextInt(avatars.size()));
    }

    static String getRandomGroupChatImage() {
        return groupChatImages.get(rnd.nextInt(groupChatImages.size()));
    }

    static String getRandomGroupChatTitle() {
        return groupChatTitles.get(rnd.nextInt(groupChatTitles.size()));
    }

    static String getRandomName() {
        return names.get(rnd.nextInt(names.size()));
    }

    static String getRandomMessage() {
        return messages.get(rnd.nextInt(messages.size()));
    }

    static String getRandomImage() {
        return images.get(rnd.nextInt(images.size()));
    }

    static boolean getRandomBoolean() {
        return rnd.nextBoolean();
    }
}

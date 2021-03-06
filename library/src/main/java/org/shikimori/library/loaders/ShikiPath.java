package org.shikimori.library.loaders;

/**
 * Created by Феофилактов on 15.10.2014.
 */
public interface ShikiPath {
    public String ID = ":id";
    public String CALENDAR = "api/calendar";
    public String COMMENTS = "api/comments";
    public String COMMENTS_ID = "api/comments/:id";
    public String MESSAGESPRIVATE = "api/messages";
    public String MESSAGESPRIVATE_ID = "api/messages/:id";
    public String ANIMES = "api/animes";
    public String ANIME_SIMILAR = "api/animes/:id/similar";
    public String ANIME_LINK = "api/animes/:id/related";
    public String SCREENSHOTS = "api/animes/:id/screenshots";
    public String ANIMES_ID = "api/animes/";
    public String MANGAS = "api/mangas";
    public String MANGA_SIMILAR = "api/mangas/:id/similar";
    public String MANGA_LINK = "api/mangas/:id/related";
    public String MANGAS_ID = "api/mangas/";
    public String TOPICS = "api/topics";
    public String UNREAD_MESSAGES = "api/users/:id/unread_messages";
    public String READ_MESSAGE = "/api/messages/mark_read";
    public String READ_ALL = "api/messages/read_all";
    public String MESSAGES = "api/users/:id/messages";
    public String FRIENDS = "api/users/:id/friends";
    public String HISTORY = "api/users/:id/history";
    public String DIALOGS = "api/dialogs";
    public String DIALOGS_ID = "api/dialogs/:id";
    public String FAVOURITES = "api/users/:id/favourites";
    public String SET_FRIEND = "api/friends/:id";
    public String SET_IGNORES = "api/ignores/:id";
    public String TOPICS_ID = "api/topics/";
    public String CHARACTER_ID = "api/characters/";
    public String CLUB = "api/clubs/:id";
//    public String CLUB_ID = "api/clubs/:id";
//    public String CLUB_ANIME = "api/clubs/:id/animes";
//    public String CLUB_MANGA = "api/clubs/:id/mangas";
//    public String CLUB_CHACTERS = "api/clubs/:id/characters";
    //    public String AUTH = "users/sign_in";
//    public String AUTH = "api/sessions";
    public String AUTH = "api/access_token";
    public String GET_AUTH_THOKEN = "api/authenticity_token";
    public String GET_USER_DATA = "api/users/whoami";
    public String GET_USER_DETAILS = "api/users/";
    public String GET_USER_ANIME_LIST = "api/users/:id/anime_rates";
    public String GET_USER_MANGA_LIST = "api/users/:id/manga_rates";
    public String GET_USERS_LIST = "api/users";
    public String GET_CLUBS_LIST = "api/clubs";
    public String GET_USER_CLUBS_LIST = "api/users/:id/clubs";
    public String SET_USER_RATE = "api/user_rates";
    public String USER_RATE_ID = "api/user_rates/:id";
    public String CLUB_IMAGES = "api/clubs/:id/images";
    public String USER_IMAGES = "api/user_images";
    public String SMILEY = "api/constants/smileys";
    public String DEVICES  = "api/devices";
    public String DEVICES_ID  = "api/devices/:id";

    public interface Prefix {
        public String JOIN = "join";
        public String LEAVE = "leave";
        public String ANIMES = "animes";
        public String MANGAS = "mangas";
        public String CHARACTERS = "characters";

    }
}

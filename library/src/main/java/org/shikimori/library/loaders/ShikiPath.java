package org.shikimori.library.loaders;

/**
 * Created by Феофилактов on 15.10.2014.
 */
public interface ShikiPath {
    public String ID = ":id";
    public String CALENDAR = "api/calendar";
    public String COMMENTS = "api/comments";
    public String MESSAGESPRIVATE = "api/messages";
    public String ANIMES = "api/animes";
    public String ANIMES_ID = "api/animes/";
    public String MANGAS = "api/mangas";
    public String MANGAS_ID = "api/mangas/";
    public String TOPICS = "api/topics";
    public String UNREAD_MESSAGES = "api/users/:id/unread_messages";
    public String READ_MESSAGE = "/api/messages/mark_read";
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
    //    public String AUTH = "users/sign_in";
//    public String AUTH = "api/sessions";
    public String AUTH = "api/access_token";
    public String GET_AUTH_THOKEN = "api/authenticity_token";
    public String GET_USER_DATA = "api/users/whoami";
    public String GET_USER_DETAILS = "api/users/";
    public String GET_USER_ANIME_LIST = "api/users/:id/anime_rates";
    public String GET_USERS_LIST = "api/users";
    public String GET_CLUBS_LIST = "api/clubs";
}

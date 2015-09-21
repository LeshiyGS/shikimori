package org.shikimori.library.objects;

/**
 * Created by Владимир on 21.09.2015.
 */
public class ActionQuote {
    private final String userId;
    private final String userName;
    private final String commentId;

    public ActionQuote(String userId, String userName, String CommentId){

        this.userId = userId;
        this.userName = userName;
        commentId = CommentId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getCommentId() {
        return commentId;
    }
}

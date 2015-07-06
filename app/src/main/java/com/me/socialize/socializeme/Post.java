package com.me.socialize.socializeme;

/**
 * Created by Toni on 3.7.2015..
 */
public class Post {
    private String m_PosterName;
    private String m_PosterEmail;
    private String m_PostContent;
    private String m_PostDate;

    public Post(String PosterName, String PosterEmail, String PostContent, String PostDate)
    {
        m_PosterName = PosterName;
        m_PosterEmail = PosterEmail;
        m_PostContent = PostContent;
        m_PostDate = PostDate;
    }

    String getPosterName(){return m_PosterName;};
    String getPosterEmail(){return m_PosterEmail;};
    String getPostContent(){return m_PostContent;};
    String getPostDate(){return m_PostDate;};

    boolean ComparePostTo(Post otherPost)
    {
        if(m_PosterName.equals(otherPost.getPosterName()) && m_PosterEmail.equals(otherPost.getPosterEmail()) &&
                m_PostContent.equals(otherPost.getPostContent()) && m_PostDate.equals(otherPost.getPostDate()))
        {
            return true;
        }
        return false;
    }
}

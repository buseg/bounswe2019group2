import React, { Component } from 'react';
import { Button } from 'antd';

import './article.scss';
import { PostWithAuthorization } from '../../common/http/httpUtil';
import history from '../../common/history';

import Comment from '../comment/Comment';

class Article extends Component {
  componentDidMount() {
    const { id, getArticle, getArticleComments } = this.props;
    getArticle(id);
    getArticleComments(id);
  }

  handleFollow = () => {
    const { user, article } = this.props;
    // eslint-disable-next-line camelcase
    const user_followed = article.author.url;
    const url = 'https://api.traiders.tk/following/';
    if (user) {
      PostWithAuthorization(url, { user_followed }, user.key)
        // eslint-disable-next-line no-console
        .then((response) => console.log(response))
        // eslint-disable-next-line no-console
        .catch((error) => console.log('Errow while following\n', error));
    } else {
      history.push('/login');
    }
  };

  render() {
    const { article, comments, user } = this.props;

    const ownArticle =
      user && article ? user.user.url === article.author.url : false;

    return (
      <div>
        {(article && (
          <div className="article-container">
            <div className="article-title">{article.title}</div>
            <div className="article-author">
              <div className="user-related">
                <div className="author-name">{`${article.author.first_name} ${article.author.last_name}`}</div>
                <div className="author-username">
                  ({article.author.username})
                </div>
              </div>
              <div className="article-related">
                {article.created_at.substring(0, 10)}
                <Button onClick={this.handleFollow} disabled={ownArticle}>
                  Follow
                </Button>
              </div>
            </div>

            <div className="article-image-container">
              <img
                className="article-image"
                src={article.image}
                alt={article.image}
              />
            </div>
            <pre className="article-content">{article.content}</pre>
            <div className="written-by" />
            <div className="article-comment">
              {comments &&
                comments.map((comment) => (
                  <Comment
                    author={comment.user.username}
                    content={comment.content}
                    createdAt={comment.created_at.substring(0, 10)}
                    image={comment.image}
                  />
                ))}
            </div>
          </div>
        )) ||
          'Loading'}
      </div>
    );
  }
}
export default Article;

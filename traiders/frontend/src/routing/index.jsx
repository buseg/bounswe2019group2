import React, { Component, Suspense } from 'react';
import { Route, Switch } from 'react-router-dom';

import Home from '../modules/home/Home';
import Login from '../modules/auth/login/Login';
import Register from '../modules/auth/register/RegisterContainer';

class Routes extends Component {
  constructor(props) {
    super(props);

    const { history } = props;
    this.unsubscribeFromHistory = history.listen(this.handleLocationChange);
  }

  componentWillUnmount() {
    if (this.unsubscribeFromHistory) {
      this.unsubscribeFromHistory();
    }
  }

  render() {
    return (
      <Suspense>
        <Switch>
          <Route exact path="/" component={Home} />
          <Route path="/login" component={Login} />
          <Route path="/register" component={Register} />
          <Route render={() => <h1>404 Page not found</h1>} />
        </Switch>
      </Suspense>
    );
  }
}

export default Routes;

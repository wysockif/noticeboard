import React from 'react';
import { Route, Switch } from 'react-router-dom';
import HomePage from '../pages/HomePage';
import LoginPage from '../pages/LoginPage';
import NoticeFormPage from '../pages/NoticeFormPage';
import NoticePage from '../pages/NoticePage';
import RegistrationPage from '../pages/RegistrationPage';
import UserProfilePage from '../pages/UserProfilePage';
import ErrorPage from '../pages/ErrorPage';

function App() {
  return (
    <div>
      <div className="container">
        <Switch>
          <Route exact path="/" component={HomePage} />
          <Route path="/login" component={LoginPage} />
          <Route path="/register" component={RegistrationPage} />
          <Route path="/user/:username" component={UserProfilePage} />
          <Route path="/notice/:id" component={NoticePage} />
          <Route path="/mynotice/:id" component={NoticeFormPage} />
          <Route path="*" component={ErrorPage} />
        </Switch>
      </div>
    </div>
  );
}

export default App;
import React from 'react';
import {Route, Switch} from 'react-router-dom';
import HomePage from '../pages/HomePage';
import LoginPage from '../pages/LoginPage';
import NoticePage from '../pages/NoticePage';
import RegistrationPage from '../pages/RegistrationPage';
import UserProfilePage from '../pages/UserProfilePage';
import ErrorPage from '../pages/ErrorPage';
import TopBar from '../components/TopBar';
import * as apiCalls from '../api/apiCalls';
import {Container, Navbar} from 'react-bootstrap';
import CreateNoticePage from '../pages/CreateNoticePage';
import EditNoticePage from '../pages/EditNoticePage';
import VerificationEmailAddressPage from "../pages/VerificationEmailAddressPage";
import ActivateAccountPage from "../pages/ActivateAccountPage";

const actions = {
    postLogin: apiCalls.login,
    postRegister: apiCalls.register
}

function App() {
    return (
        <div>
            <TopBar/>
            <div>
                <Container>
                    <Switch>
                        <Route exact path="/" render={(props) => <HomePage {...props} key={Date.now()}/>}/>
                        <Route path="/login" component={props => <LoginPage {...props} actions={actions}/>}/>
                        <Route path="/register"
                               component={props => < RegistrationPage  {...props} actions={actions}/>}/>
                        <Route path="/user/:username"
                               render={(props) => <UserProfilePage {...props} key={Date.now()}/>}/>
                        <Route path="/notice/new" component={CreateNoticePage}/>
                        <Route path="/notice/edit/:id" component={EditNoticePage}/>
                        <Route path="/notice/:id" component={NoticePage}/>
                        <Route path="/activate" component={ActivateAccountPage}/>
                        <Route path="/verify/:token" component={VerificationEmailAddressPage}/>
                        <Route path="/verify" component={VerificationEmailAddressPage}/>
                        <Route path="*" component={ErrorPage}/>
                    </Switch>
                </Container>
                <div>
                </div>
                <div className="footerWrap">
                    <Navbar sticky="bottom" variant="light" expand="lg"
                            className="shadow-sm border border-2 rounded"
                            bg="light" style={{minHeight: '65px'}}>
                        <div className="mx-auto">
                            <div className="text-center text-muted">
                                <small>&copy; 2021 Noticeboard.pl</small>
                            </div>
                            <div className="text-center text-muted">
                                <small>Franciszek Wysocki</small>
                            </div>
                        </div>
                    </Navbar>
                </div>
            </div>
        </div>
    );
}

export default App;
import React, {Component} from 'react';
import {connect} from 'react-redux';
import noticeboardLogo from '../assets/logo.png';
import {Container, Image, Nav, Navbar} from 'react-bootstrap';
import {Link, withRouter} from 'react-router-dom';

class TopBar extends Component {

    onClickLogout = () => {
        const logoutAction = {
            type: 'LOGOUT_SUCCESS'
        }
        this.props.dispatch(logoutAction);
        this.props.history.replace('/');
    };

    onClickLogo = () => {
        const current = this.props.location.pathname;
        if (current === '/') {
            this.props.history.replace('/');
        } else {
            this.props.history.push('/');
        }
    }

    onClickMyProfile = () => {
        const username = this.props.user.username;
        const current = this.props.location.pathname;
        if (current === `/user/${username}`) {
            this.props.history.replace(`/user/${username}`);
        } else {
            this.props.history.push(`/user/${username}`);
        }
    }

    render() {
        return (
            <Navbar variant="light" expand="lg"
                    className="shadow-sm border border-2 rounded mb-3 mt-1 "
                    bg="light"
            >
                <Container className="col-sm-11 col-md-9">
                    <Navbar.Brand style={{cursor: 'pointer'}} onClick={this.onClickLogo} >
                        <div>
                            <Image src={noticeboardLogo} alt="noticeboard" className="ms-3 my-1" fluid width="140"/>
                        </div>
                    </Navbar.Brand>
                    <Navbar.Toggle aria-controls="responsive-navbar-nav" className="my-1 mx-3 p-1"/>
                    <Navbar.Collapse id="responsive-navbar-nav" className="justify-content-end">
                        {!this.props.user.isLoggedIn && <Nav className="ms-auto">
                            <Link to="/register" replace={'/register' === this.props.location.pathname}
                                  className="nav-link ms-3">Rejestracja</Link>
                            <Link to="/login" replace={'/login' === this.props.location.pathname}
                                  className="nav-link ms-3 me-3">Logowanie</Link>
                        </Nav>}
                        {this.props.user.isLoggedIn && <Nav className="ms-auto">
                            <Link to={"/notice/new"} replace={'/notice/new' === this.props.location.pathname}
                                  className="nav-link ms-3">Dodaj ogłoszenie</Link>

                            <div style={{cursor: 'pointer'}} onClick={this.onClickMyProfile}
                                  className="nav-link ms-3">
                                Moja tablica
                            </div>

                            {/*<Link to={"/user/" + this.props.user.username}*/}
                            {/*      replace={'/notice/user/' + this.props.user.username === this.props.location.pathname}*/}
                            {/*      className="nav-link ms-3">*/}
                            {/*    Moja tablica*/}
                            {/*</Link>*/}
                            <div className="nav-link ms-3 me-3" style={{cursor: 'pointer'}}
                                 onClick={this.onClickLogout}>
                                Wyloguj się
                            </div>
                        </Nav>}
                    </Navbar.Collapse>
                </Container>
            </Navbar>

        );
    }
}

const mapStateToProps = state => {
    return {
        user: state
    }
}

export default withRouter(connect(mapStateToProps)(TopBar));
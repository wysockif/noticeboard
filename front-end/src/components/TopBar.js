import React, { Component } from 'react';
import { connect } from 'react-redux';
import noticeboardLogo from '../assets/logo.png';
import { Container, Image, Navbar, Nav } from 'react-bootstrap';
import { Link, withRouter } from 'react-router-dom';

class TopBar extends Component {

    onClickLogout = () => {
        const logoutAction = {
            type: 'LOGOUT_SUCCESS'
        }
        this.props.dispatch(logoutAction);
        this.props.history.replace('/');
    };

    render() {
        return (
            <Navbar variant="light" expand="lg"
                className="shadow-sm border border-2 rounded mb-3 mt-1 "
                bg="light"
            >
                <Container className="col-sm-11 col-md-9">
                    <Navbar.Brand >
                        <Link to="/" replace>
                            <Image src={noticeboardLogo} alt="noticeboard" className="ms-3 my-1" fluid width="140" />
                        </Link>
                    </Navbar.Brand>
                    <Navbar.Toggle aria-controls="responsive-navbar-nav" className="my-1 mx-3 p-1" />
                    <Navbar.Collapse id="responsive-navbar-nav" className="justify-content-end">
                        {!this.props.user.isLoggedIn && <Nav className="ms-auto">
                            <Link to="/register" replace className="nav-link ms-3">Rejestracja</Link>
                            <Link to="/login" replace className="nav-link ms-3 me-3" >Logowanie</Link>
                        </Nav>}
                        {this.props.user.isLoggedIn && <Nav className="ms-auto">
                            <Link to={"/notice/new"} replace className="nav-link ms-3">Dodaj ogłoszenie</Link>
                            <Link to={"/user/" + this.props.user.username} replace className="nav-link ms-3">Moja tablica</Link>
                            <div className="nav-link ms-3 me-3" style={{ cursor: 'pointer' }} onClick={this.onClickLogout}>
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
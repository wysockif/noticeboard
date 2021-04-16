import React, { Component } from 'react';
import noticeboardLogo from '../assets/logo.png';
import { Image, Navbar, Nav } from 'react-bootstrap';
import { Link } from 'react-router-dom';


class TopBar extends Component {
    render() {
        return (
            <Navbar bg="light" variant="light" expand="lg" className="shadow-sm mb-3 mt-1">
                <Navbar.Brand >
                    <Link to="/">
                        <Image src={noticeboardLogo} alt="noticeboard" className="ms-2" fluid width="180" />
                    </Link>
                </Navbar.Brand>
                <Navbar.Toggle aria-controls="responsive-navbar-nav" />
                <Navbar.Collapse id="responsive-navbar-nav" className="justify-content-end">
                    <Nav className="ms-auto">
                        <Link to="/register" className="nav-link ms-3">Rejestracja</Link>
                        <Link className="nav-link ms-3 me-3" to="/login">Logowanie</Link>
                    </Nav>
                </Navbar.Collapse>
            </Navbar>

        );
    }
}

export default TopBar;
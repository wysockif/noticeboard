import React, { Component } from 'react';
import noticeboardLogo from '../assets/logo.png';
import { Container, Image, Navbar, Nav } from 'react-bootstrap';
import { Link } from 'react-router-dom';


class TopBar extends Component {
    render() {
        return (
            <Navbar variant="light" expand="lg"
                className="shadow-sm border border-2 rounded mb-3 mt-1 "
                // style={{ backgroundColor: "#F9F9F9" }}
                bg="light"
            >
                <Container >
                <Navbar.Brand >
                    <Link to="/">
                        <Image src={noticeboardLogo} alt="noticeboard" className="ms-3 my-1" fluid width="140" />
                    </Link>
                </Navbar.Brand>
                <Navbar.Toggle aria-controls="responsive-navbar-nav" className="my-1 mx-3 p-1" />
                <Navbar.Collapse id="responsive-navbar-nav" className="justify-content-end">
                    <Nav className="ms-auto">
                        <Link to="/register" className="nav-link ms-3">Rejestracja</Link>
                        <Link className="nav-link ms-3 me-3" to="/login">Logowanie</Link>
                    </Nav>
                </Navbar.Collapse>
                </Container>
            </Navbar>

        );
    }
}

export default TopBar;
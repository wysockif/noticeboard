import React from 'react';
import {Navbar} from "react-bootstrap";

const Footer = () => {
    return (
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
    );
};

export default Footer;

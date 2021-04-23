import React, {Component} from 'react';
import {Collapse} from "react-bootstrap";

class UserProfilePageCollapse extends Component {
    render() {
        return (
            <div className="text-center">
                <Collapse in={this.props.open}>
                    <div id="example-collapse-text">
                        Jakieś opcje edycji
                    </div>
                </Collapse>
            </div>
        );
    }
}

export default UserProfilePageCollapse;
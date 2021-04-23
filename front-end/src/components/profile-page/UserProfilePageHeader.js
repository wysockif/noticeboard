import React, {Component} from 'react';
import {Button, Card} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import UserProfilePageCollapse from "./UserProfilePageCollapse";
import UserProfilePageInfo from "./UserProfilePageInfo";

class UserProfilePageHeader extends Component {
    state = {
        open: false
    }

    render() {
        return ((
            <Card.Header className="text-center">
                <div className="col-10 col-sm-9 col-md-8 mx-auto">
                    {this.props.user &&
                    <UserProfilePageInfo user={this.props.user}/>}
                    <div className="text-center mt-2">
                        <Button
                            size="sm"
                            onClick={() => this.setState({open: !this.state.open})}
                            aria-controls="example-collapse-text"
                            aria-expanded={this.props.open}
                            variant="light"
                        >
                            {!this.state.open && <div className="text-muted">
                                <small>Pokaż opcje edycji</small>
                                <FontAwesomeIcon icon="arrow-down" className="mx-1"/>
                            </div>}
                            {this.state.open && <div className="text-muted">
                                <small>Ukryj opcje edycji</small>
                                <FontAwesomeIcon icon="arrow-up" className="mx-1"/>
                            </div>}

                        </Button>
                    </div>
                    <UserProfilePageCollapse open={this.state.open}/>
                </div>
            </Card.Header>
        ))
    }
}

export default UserProfilePageHeader;
import React, {Component} from 'react';
import {Button, Card, Collapse, Image} from "react-bootstrap";
import defaultProfilePicture from "../assets/default-profile-image.jpeg";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

class UserProfilePageHeader extends Component {
    state = {
        open: false
    }

    render() {
        return ((
            <Card.Header className="text-center">
                <div className="col-10 col-sm-9 col-md-8 mx-auto">
                    {this.props.user && <span>
                                    <Image src={this.props.profileImage} alt="profile-picture"
                                           md={4}
                                           className="shadow-sm"
                                           width="100"
                                           height="100"
                                           roundedCircle
                                           onError={event => event.target.src = defaultProfilePicture}
                                    />
                                    <Card.Title
                                        className="mt-1">{this.props.user.firstName} {this.props.user.lastName}</Card.Title>
                                    <Card.Subtitle
                                        className="text-muted">{`@${this.props.user.username}`}</Card.Subtitle>
                                </span>}
                    <div className="text-center mt-2">
                        <Button
                            size="sm"
                            onClick={() => this.setState({open: !this.props.open})}
                            aria-controls="example-collapse-text"
                            aria-expanded={this.props.open}
                            variant="light"
                        >
                            {!this.props.open && <div className="text-muted">
                                <small>Pokaż opcje edycji</small>
                                <FontAwesomeIcon icon="arrow-down" className="mx-1"/>
                            </div>}
                            {this.props.open && <div className="text-muted">
                                <small>Ukryj opcje edycji</small>
                                <FontAwesomeIcon icon="arrow-up" className="mx-1"/>
                            </div>}

                        </Button>
                    </div>
                    <div className="text-center">
                        <Collapse in={this.props.open}>
                            <div id="example-collapse-text">
                                Jakieś opcje edycji
                            </div>
                        </Collapse>
                    </div>
                </div>
            </Card.Header>
        ))
    }
}

export default UserProfilePageHeader;
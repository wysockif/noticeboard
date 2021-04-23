import React, {Component} from 'react';
import {Button, Card, Collapse, Image, Alert} from 'react-bootstrap';
import ErrorAlert from "../components/ErrorAlert";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import * as apiCalls from '../api/apiCalls';
import userNotFoundImage from '../assets/user-not-found.jpeg';
import defaultProfilePicture from '../assets/default-profile-image.jpeg';
import temp from '../assets/temp.svg';


class UserProfilePage extends Component {
    state = {
        errorMessage: false,
        user: undefined
    }

    componentDidMount() {
        this.loadUserToState();
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (this.props.match.params.username !== prevProps.match.params.username) {
            this.loadUserToState();
        }
    }

    loadUserToState() {
        const username = this.props.match.params.username;
        if (!username) {
            return;
        }
        this.setState({errorMessage: false});
        apiCalls.getUser(username)
            .then(response => {
                this.setState({user: response.data});
            })
            .catch(error => {
                if (error.response.status === 404) {
                    this.setState({errorMessage: true})
                } else {
                    this.setState({connectionError: true})
                }
            });
    }

    userNotFoundAlert() {
        return (
            <ErrorAlert image={userNotFoundImage}/>
        );
    }

    render() {
        if (this.state.errorMessage) {
            return this.userNotFoundAlert();
        } else if (this.state.connectionError) {
            return (
                <div className="text-center">
                    <Alert className="col-5 mx-auto" variant="danger">Nie można załadować strony. Spróbuj ponownie
                        później.</Alert>
                </div>
            );
        }

        let profileImage = defaultProfilePicture;
        if (this.state.user && this.state.user.image) {
            profileImage = this.state.user.image;
        }
        let className = "col-12 col-md-6 col-lg-4 my-2"
        return (
            <div data-testid="userprofilepage">

                <div data-testid="homepage">
                    <Card>
                        <Card.Header className="text-center">
                            <div className="col-10 col-sm-9 col-md-8 mx-auto">
                                {this.state.user && <span>
                                    <Image src={profileImage} alt="profile-picture"
                                           md={4}
                                           className="shadow-sm"
                                           width="100"
                                           height="100"
                                           roundedCircle
                                           onError={event => event.target.src = defaultProfilePicture}
                                    />
                                    <Card.Title
                                        className="mt-1">{this.state.user.firstName} {this.state.user.lastName}</Card.Title>
                                    <Card.Subtitle
                                        className="text-muted">{`@${this.state.user.username}`}</Card.Subtitle>
                                </span>}
                                <div className="text-center mt-2">
                                    <Button
                                        size="sm"
                                        onClick={() => this.setState({open: !this.state.open})}
                                        aria-controls="example-collapse-text"
                                        aria-expanded={this.state.open}
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
                                <div className="text-center">
                                    <Collapse in={this.state.open}>
                                        <div id="example-collapse-text">
                                            Jakieś opcje edycji
                                        </div>
                                    </Collapse>
                                </div>
                            </div>
                        </Card.Header>


                        <div className="row m-4">
                            <div className={className}>
                                <Card className="col-11 mx-auto list-group-item-action" style={{cursor: 'pointer'}}>
                                    <div className="mx-auto">
                                        <h6><FontAwesomeIcon icon="map-pin"/></h6>
                                    </div>
                                    <Card.Img variant="top" src={temp}/>
                                    <Card.Body>
                                        <Card.Title>Sprzedam opla</Card.Title>
                                        <Card.Text as="span">
                                            <div><FontAwesomeIcon icon="wallet" className="mx-1"/>2000 zł</div>
                                            <div><FontAwesomeIcon icon="map-marker-alt" className="mx-1"/>Warszawa</div>
                                        </Card.Text>
                                    </Card.Body>
                                    <Card.Footer>
                                        <small className="text-muted">Opublikowano: 3 minuty temu</small>
                                    </Card.Footer>
                                </Card>
                            </div>
                            <div className={className}>
                                <Card className="col-11 mx-auto list-group-item-action" style={{cursor: 'pointer'}}>
                                    <div className="mx-auto">
                                        <h6><FontAwesomeIcon icon="map-pin"/></h6>
                                    </div>
                                    <Card.Img variant="top" src={temp}/>
                                    <Card.Body>
                                        <Card.Title>Laptop</Card.Title>
                                        <Card.Text as="span">
                                            <div>2200 zł</div>
                                            <div>Kraków</div>
                                        </Card.Text>
                                    </Card.Body>
                                    <Card.Footer>
                                        <small className="text-muted">Opublikowano: 3 minuty temu</small>
                                    </Card.Footer>
                                </Card>
                            </div>
                            <div className={className}>
                                <Card className="col-11 mx-auto list-group-item-action" style={{cursor: 'pointer'}}>
                                    <div className="mx-auto">
                                        <h6><FontAwesomeIcon icon="map-pin"/></h6>
                                    </div>

                                    <Card.Img variant="top" src={temp}/>
                                    <Card.Body>
                                        <Card.Title>Zabawki dla dziecka</Card.Title>
                                        <Card.Text as="span">
                                            <div>140 zł</div>
                                            <div>Warszawa</div>
                                        </Card.Text>
                                    </Card.Body>
                                    <Card.Footer>
                                        <small className="text-muted">Opublikowano: 3 minuty temu</small>
                                    </Card.Footer>
                                </Card>
                            </div>

                        </div>
                    </Card>
                </div>


            </div>
        );
    }
}

UserProfilePage.defaultProps = {
    match: {
        params: {}
    }
};

export default UserProfilePage;
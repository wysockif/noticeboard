import React, {Component} from 'react';
import {Button, Card, Carousel, Container, Image, Spinner} from "react-bootstrap";
import * as apiCalls from "../api/apiCalls";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import moment from "moment";
import {Link} from "react-router-dom";
import DeleteModal from "../components/DeleteModal";
import {connect} from "react-redux";
import defaultProfilePicture from "../assets/default-profile-image.jpeg";


class NoticePage extends Component {


    state = {
        isNoticeDetailsLoading: true,
        isUserDetailsLoading: true,
        notice: {
            id: '',
            title: '',
            location: '',
            price: '',
            description: '',
            primaryImage: '',
            secondaryImage: '',
            tertiaryImage: '',
            createdAt: ''
        },
        user: {
            firstName: '',
            lastName: '',
            email: '',
            image: '',
            username: '',
            id: ''
        },
        index: 0,
        show: false,
        ongoingApiCall: false,
        errorMessageInModal: undefined
    }

    paragraphs = 0;

    componentDidMount() {
        this.setState({isNoticeDetailsLoading: true, isUserDetailsLoading: true})
        const noticeId = this.props.match.params.id;
        apiCalls.getNotice(noticeId)
            .then(response => {
                this.setState({notice: response.data, isNoticeDetailsLoading: false});
            })
            .catch(error => {

            });
        apiCalls.getUserByNoticeId(noticeId)
            .then(response => {
                this.setState({user: response.data, isUserDetailsLoading: false});
            })
            .catch(error => {

            });
    }

    handleSelect = (selectedIndex) => {
        this.setState({index: selectedIndex});
    };

    handleClose = () => {
        this.setState({show: false});
    };

    handleShow = () => {
        this.setState({show: true, errorMessageInModal: undefined});
    };

    onClickDelete = () => {
        this.setState({ongoingApiCall: true, errorMessageInModal: undefined})
        apiCalls.deleteNotice(this.state.notice.id)
            .then(() => {
                this.setState({show: false, ongoingApiCall: false});
                console.log('here')
                this.props.history.push(`/user/${this.state.user.username}`);
            })
            .catch(error => {
                console.log(error)
                this.setState({ongoingApiCall: false, errorMessageInModal: error.response.data.message});
            });
    };

    onClickEdit = () => {
        this.props.history.push({
            pathname: `/notice/edit/${this.state.notice.id}`,
            state: {
                notice: this.state.notice,
                userEmail: this.state.user.email,
                userId: this.state.user.id
            }
        });
    }

    getMainContent = () => {
        const momentDate = moment(new Date(this.state.notice.createdAt));
        momentDate.locale('pl');
        let profileImage;
        if (this.state.user && this.state.user.image) {
            profileImage = "/images/profile/" + this.state.user.image;
        } else {
            profileImage = defaultProfilePicture;
        }
        return (
            <Card>
                <Card.Header className="text-center">
                    <h4 className="my-2">{this.state.notice.title}</h4>
                </Card.Header>
                <Card.Body className="col-12 col-sm-11 col-md-10 mx-auto">
                    <div className="row justify-content-center">
                        <div className="col-11 col-lg-6">
                            <Carousel
                                activeIndex={this.state.index}
                                onSelect={this.handleSelect}
                                slide={false}
                                className="carousel-dark"
                                interval={10000}
                            >
                                <Carousel.Item style={{maxHeight: "375px"}}>
                                    {this.state.notice.primaryImage &&
                                    <Image thumbnail
                                           className="d-block w-100"
                                           src={`/images/notice/${this.state.notice.primaryImage}`}
                                           alt="First slide"
                                    />}
                                </Carousel.Item>
                                <Carousel.Item style={{maxHeight: "375px"}}>
                                    {this.state.notice.secondaryImage &&
                                    <Image thumbnail
                                           className="d-block w-100"
                                           src={`/images/notice/${this.state.notice.secondaryImage}`}
                                           alt="Second slide"
                                    />}
                                </Carousel.Item>
                                <Carousel.Item style={{maxHeight: "375px"}}>
                                    {this.state.notice.tertiaryImage &&
                                    <Image thumbnail
                                           className="d-block w-100"
                                           src={`/images/notice/${this.state.notice.tertiaryImage}`}
                                           alt="Third slide"
                                    />}
                                </Carousel.Item>
                            </Carousel>
                        </div>
                        <div className="col-10 col-lg-5 align-self-center">
                            <div className="fs-5 my-2">
                                <small>
                                    <FontAwesomeIcon icon="wallet" className="ms-1 me-1 pe-1"/>
                                    Cena:
                                </small>
                                <div className="fw-bold ms-2">{this.state.notice.price} zł</div>
                            </div>
                            <div className="fs-5 mb-2">
                                <small>
                                    <FontAwesomeIcon icon="map-marker-alt" className="ms-1 me-2"/>
                                    Lokalizacja:
                                </small>
                                <div className="fw-bold ms-2">{this.state.notice.location}</div>
                            </div>
                            <div className="fs-5 mb-2">
                                <small>
                                    <FontAwesomeIcon icon="calendar-alt" className="ms-1 me-1"/>
                                    Data opublikowania:
                                </small>
                                <div className="fw-bold ms-2">
                                    {momentDate.format("DD.MM.YYYY")}
                                </div>
                            </div>
                            <div className="fs-5 mb-2">
                                <small>
                                    <FontAwesomeIcon icon="clock" className="ms-1 me-1"/>
                                    Godzina opublikowania:
                                </small>
                                <div className="fw-bold ms-2">
                                    {momentDate.format("HH:MM")}
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="fs-5 col-11 mx-auto mt-4">
                        {this.state.notice.description.split('\n').map(str => <p key={this.paragraphs++}>{str}</p>)}
                    </div>

                    <div className="row justify-content-center">
                        <div className="col-sm-5 mt-4 text-center text-sm-start pt-3">
                            <h5><FontAwesomeIcon icon="envelope"/> Kontakt mailowy:</h5>
                            <h5>{this.state.user.email}</h5>

                        </div>
                        <div className="col-sm-6 mt-4 py-2 text-center text-sm-end">
                            {this.state.user.username && <Link
                                to={`/user/${this.state.user.username}`}
                                className="btn btn-outline-secondary px-4 mt-3"
                            >
                                <div>
                                    {`${this.state.user.firstName} ${this.state.user.lastName}`}
                                    <Image roundedCircle src={profileImage} width="40"
                                           height="40" className="ms-2 border border-secondary"/>
                                </div>
                            </Link>}
                        </div>
                    </div>
                </Card.Body>
                <Card.Footer className="text-center">
                    {this.state.user.username === this.props.loggedInUserUsername &&
                    <div>
                        <Button className="px-5 m-1" variant="outline-secondary" onClick={this.onClickEdit}>
                            <FontAwesomeIcon icon="edit" className="me-1"/>Edytuj
                        </Button>
                        <Button className="px-5 m-1" variant="outline-secondary" onClick={this.handleShow}>
                            <FontAwesomeIcon icon="trash-alt" className="me-1"/>Skasuj
                        </Button>
                        <DeleteModal
                            show={this.state.show}
                            onClickCancel={this.handleClose}
                            onClickDelete={this.onClickDelete}
                            ongoingApiCall={this.state.ongoingApiCall}
                            errorMessage={this.state.errorMessageInModal}
                        />
                    </div>
                    }
                </Card.Footer>
            </Card>
        );
    }

    getSpinner = () => {
        return (<div className="text-center">
            <Spinner animation="border" size="sm" role="status" className="ms-1">
                <span className="sr-only">Loading...</span>
            </Spinner>
        </div>)
    }

    render() {
        const content = (this.state.isNoticeDetailsLoading || this.state.isUserDetailsLoading)
            ? this.getSpinner() : this.getMainContent();

        return (
            <Container data-testid="noticepage" className="my-3">
                {content}
            </Container>
        );
    }
}

NoticePage.defaultProps = {
    match: {
        params: {
            id: 1
        }
    }
}

const mapStateToProps = state => {
    return {
        isLoggedIn: state.isLoggedIn,
        loggedInUserUsername: state.username
    }
}

export default connect(mapStateToProps)(NoticePage);
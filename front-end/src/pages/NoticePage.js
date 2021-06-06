import React, {Component} from 'react';
import {Button, Card, Carousel, Container, Image, Spinner} from "react-bootstrap";
import * as apiCalls from "../api/apiCalls";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import moment from "moment";
import {Link} from "react-router-dom";
import DeleteNoticeModal from "../components/DeleteNoticeModal";
import {connect} from "react-redux";
import defaultNoticeImage from "../assets/default-notice-image.jpg";
import defaultProfilePicture from "../assets/default-profile-image.jpeg";


class NoticePage extends Component {

    notice = {
        id: '',
        title: '',
        location: '',
        price: '',
        description: '',
        primaryImage: '',
        secondaryImage: '',
        tertiaryImage: '',
        createdAt: ''
    }

    user = {
        firstName: '',
        lastName: '',
        email: '',
        image: '',
        username: '',
        id: ''
    }

    paragraphs = 0;

    state = {
        isNoticeDetailsLoading: true,
        isUserDetailsLoading: true,
        errorMessage: '',
        notice: {...this.notice},
        user: {...this.user},
        index: 0,
        show: false,
        ongoingApiCall: false,
        errorMessageInModal: undefined
    }

    componentDidMount() {
        this.setState({isNoticeDetailsLoading: true, isUserDetailsLoading: true, apiErrorMessage: false})
        const noticeId = this.props.match.params.id;
        if (/^\d+$/.test(noticeId)) {
            this.loadNotices(noticeId);
            this.loadUser(noticeId);
        } else {
            this.setState({errorMessage: 'Nie znaleziono ogłoszenia.'})
        }
    }

    loadNotices = (noticeId) => {
        apiCalls.getNotice(noticeId)
            .then(response => {
                this.setState({notice: response.data, isNoticeDetailsLoading: false});
            })
            .catch(apiError => {
                let message = 'Wystąpił błąd podczas ładowania ogłoszenia. Spróbuj ponownie później.';
                if (apiError.response && apiError.response.status && apiError.response.status === 404) {
                    message = 'Nie znaleziono ogłoszenia.';
                }
                this.setState({
                    isNoticeDetailsLoading: false,
                    isUserDetailsLoading: false,
                    errorMessage: message
                });
            });
    }

    loadUser = (noticeId) => {
        apiCalls.getUserByNoticeId(noticeId)
            .then(response => {
                this.setState({user: response.data, isUserDetailsLoading: false});
            })
            .catch(() => {
                this.setState({
                    isNoticeDetailsLoading: false,
                    isUserDetailsLoading: false,
                    errorMessage: 'Wystąpił błąd podczas ładowania ogłoszenia. Spróbuj ponownie później.'
                });
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
                this.props.history.push(`/user/${this.state.user.username}`);
            })
            .catch(error => {
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
            <Card style={{marginBottom: "90px"}}>
                <Card.Header className="text-center">
                    <h4 className="my-2">{this.state.notice.title}</h4>
                </Card.Header>
                <Card.Body className="col-12 col-sm-11 col-md-10 mx-auto">
                    <div className="row justify-content-center">
                        <div className="col-lg-7">
                            <Carousel
                                activeIndex={this.state.index}
                                onSelect={this.handleSelect}
                                slide={false}
                                className="carousel-dark"
                                interval={10000}
                            >
                                <Carousel.Item style={{maxHeight: "430px"}}>
                                    {this.state.notice.primaryImage &&
                                    <Image
                                           className="d-block w-100"
                                           src={`/images/notice/${this.state.notice.primaryImage}`}
                                           alt="First slide"
                                           onError={event => event.target.src = defaultNoticeImage}
                                    />}
                                </Carousel.Item>
                                <Carousel.Item style={{maxHeight: "430px"}}>
                                    {this.state.notice.secondaryImage &&
                                    <Image
                                           className="d-block w-100"
                                           src={`/images/notice/${this.state.notice.secondaryImage}`}
                                           alt="Second slide"
                                           onError={event => event.target.src = defaultNoticeImage}
                                    />}
                                </Carousel.Item>
                                <Carousel.Item style={{maxHeight: "430px"}}>
                                    {this.state.notice.tertiaryImage &&
                                    <Image
                                           className="d-block w-100"
                                           src={`/images/notice/${this.state.notice.tertiaryImage}`}
                                           alt="Third slide"
                                           onError={event => event.target.src = defaultNoticeImage}
                                    />}
                                </Carousel.Item>
                            </Carousel>
                        </div>
                        <div className="col-sm-11 col-lg-4 align-self-center">
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
                                    {momentDate.format("LT")}
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="fs-5 col-sm-11 mx-auto mt-4 px-2">
                        {this.state.notice.description.split('\n').map(str => <p key={this.paragraphs++}>{str}</p>)}
                    </div>

                    <div className="row justify-content-center">
                        <div className="col-sm-5 mt-4 text-center pt-3">
                            <h5><FontAwesomeIcon icon="envelope"/> Kontakt mailowy:</h5>
                            <h5>{this.state.user.email}</h5>

                        </div>
                    </div>
                </Card.Body>
                {this.state.user.username === this.props.loggedInUserUsername &&

                <Card.Footer className="text-center">
                    <div>
                        <Button className="px-5 m-1 option-btn" variant="outline-secondary" onClick={this.onClickEdit}>
                            <FontAwesomeIcon icon="edit" className="me-1"/>Edytuj
                        </Button>
                        <Button className="px-5 m-1 option-btn" variant="outline-secondary" onClick={this.handleShow}>
                            <FontAwesomeIcon icon="trash-alt" className="me-1"/>Skasuj
                        </Button>
                        <DeleteNoticeModal
                            show={this.state.show}
                            onClickCancel={this.handleClose}
                            onClickDelete={this.onClickDelete}
                            ongoingApiCall={this.state.ongoingApiCall}
                            errorMessage={this.state.errorMessageInModal}
                        />
                    </div>
                </Card.Footer>}

                {this.state.user.username !== this.props.loggedInUserUsername &&
                <Card.Footer className="text-center">
                    <div>
                        <div className="col-sm-6 text-center mx-auto">
                            {this.state.user.username && <Link
                                to={`/user/${this.state.user.username}`}
                                className="btn btn-outline-secondary profile-btn shadow-sm"
                            >
                                <div>
                                    <span>{`${this.state.user.firstName} ${this.state.user.lastName}`}</span>
                                    <Image roundedCircle src={profileImage} width="40"
                                           height="40" className="ms-2 shadow-sm"/>
                                </div>
                            </Link>}
                        </div>
                    </div>
                </Card.Footer>}
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

    getErrorMessage = () => {
        return <div className="text-center text-muted">
            {this.state.errorMessage}
        </div>;
    }

    render() {
        let content;
        if (this.state.errorMessage) {
            content = this.getErrorMessage();
        } else if (this.state.isNoticeDetailsLoading || this.state.isUserDetailsLoading) {
            content = this.getSpinner();
        } else {
            content = this.getMainContent();
        }

        return (
            <Container data-testid="noticepage" className="my-3" style={{marginBottom: "90px"}}>
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
import React, {Component} from 'react';
import {Card, Carousel, Container, Image} from "react-bootstrap";
import * as apiCalls from "../api/apiCalls";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import moment from "moment";
import {Link} from "react-router-dom";

class NoticePage extends Component {


    state = {
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
            image: ''
        },
        index: 0,
    }

    paragraphs = 0;

    componentDidMount() {
        const noticeId = this.props.match.params.id;
        apiCalls.getNotice(noticeId)
            .then(response => {
                this.setState({notice: response.data});
            })
            .catch(error => {

            });
        apiCalls.getUserByNoticeId(noticeId)
            .then(response => {
                this.setState({user: response.data});
            })
            .catch(error => {

            });
    }

    handleSelect = (selectedIndex) => {
        this.setState({index: selectedIndex});
    };

    render() {
        const momentDate = moment(new Date(this.state.notice.createdAt));
        momentDate.locale('pl');

        return (
            <Container data-testid="noticepage" className="my-3">
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
                                    <Carousel.Item>
                                        {this.state.notice.primaryImage &&
                                        <Image thumbnail
                                               className="d-block w-100"
                                               src={`/images/notice/${this.state.notice.primaryImage}`}
                                               alt="First slide"
                                        />}
                                    </Carousel.Item>
                                    <Carousel.Item>
                                        {this.state.notice.secondaryImage &&
                                        <Image thumbnail
                                               className="d-block w-100"
                                               src={`/images/notice/${this.state.notice.secondaryImage}`}
                                               alt="Second slide"
                                        />}
                                    </Carousel.Item>
                                    <Carousel.Item>
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

                        <div className="col-10 mx-auto mt-4 pe-2 text-end">
                            <h5>Zapraszam do kontaktu mailowego:</h5>
                            <h5>{this.state.user.email}</h5>
                        </div>
                    </Card.Body>
                    <Card.Footer>
                        <div className="col-8 mx-auto text-end">
                            {this.state.user.image && <Link
                                to={`/user/${this.state.user.username}`}
                                className="btn btn-outline-secondary px-4"
                            >
                                {`${this.state.user.firstName} ${this.state.user.lastName}`}
                                <Image roundedCircle src={`/images/profile/${this.state.user.image}`} width="40"
                                       height="40" className="ms-2"/>
                            </Link>}
                        </div>
                    </Card.Footer>
                </Card>
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

export default NoticePage;
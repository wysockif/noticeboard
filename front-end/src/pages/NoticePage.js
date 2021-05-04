import React, {Component} from 'react';
import {Card, Carousel, Container, Image} from "react-bootstrap";
import * as apiCalls from "../api/apiCalls";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import moment from "moment";

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
            firstName: 'Franek',
            lastName: 'Wysocki',
            email: 'mail@mail.com'
        },
        index: 0
    }

    componentDidMount() {
        const id = this.props.match.params.id;
        apiCalls.getNotice(id)
            .then(response => {
                this.setState({notice: response.data});
            })
            .catch(error => {

            });
        apiCalls.getUserByNoticeId(id);
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
                        <h5 className="mb-2">{this.state.notice.title}</h5>
                    </Card.Header>
                    <Card.Body className="col-10 mx-auto">
                        <div className="row justify-content-center">
                            <div className="col-11 col-sm-10 col-md-7 col-lg-6">
                                <Carousel
                                    activeIndex={this.state.index}
                                    onSelect={this.handleSelect}
                                    slide={false}
                                    className="carousel-dark"
                                    interval={10000}
                                >
                                    <Carousel.Item>
                                        <Image thumbnail
                                               className="d-block w-100"
                                               src={`/images/notice/${this.state.notice.primaryImage}`}
                                               alt="First slide"
                                        />
                                    </Carousel.Item>
                                    <Carousel.Item>
                                        <Image thumbnail
                                               className="d-block w-100"
                                               src={`/images/notice/${this.state.notice.secondaryImage}`}
                                               alt="Second slide"
                                        />
                                    </Carousel.Item>
                                    <Carousel.Item>
                                        <Image thumbnail
                                               className="d-block w-100"
                                               src={`/images/notice/${this.state.notice.tertiaryImage}`}
                                               alt="Third slide"
                                        />
                                    </Carousel.Item>
                                </Carousel>
                            </div>
                            <div className="col-11 col-sm-10 col-md-4 align-self-center">
                                <div className="fs-5 mb-2">
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
                        <div className="fs-5 col-10 mx-auto mt-4">
                            {this.state.notice.description}
                        </div>

                    </Card.Body>
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
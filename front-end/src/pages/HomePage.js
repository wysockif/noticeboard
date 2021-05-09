import React, {Component} from 'react';
import {Button, Card, Collapse, FormControl, InputGroup, Pagination} from 'react-bootstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import NoticeboardItem from "../components/NoticeboardItem";
import * as apiCalls from "../api/apiCalls";


class HomePage extends Component {
    state = {
        open: false,
        page: {
            content: [],
            number: 0,
            size: 18,
            totalPages: 1
        }
    }

    componentDidMount() {
        apiCalls.getNotices()
            .then(response => {
                this.setState({page: response.data});
            })
            .catch(error => {

            });
    }

    render() {
        return (
            <div data-testid="homepage">
                <Card>
                    <Card.Header>
                        <div className="col-10 col-sm-9 col-md-8 mx-auto">
                            <InputGroup className="my-2">

                                <InputGroup.Prepend>
                                    <InputGroup.Text>
                                        <FontAwesomeIcon icon="search" className="my-1"/>
                                    </InputGroup.Text>
                                </InputGroup.Prepend>
                                <FormControl
                                    placeholder="Czego szukasz?"
                                />
                                <InputGroup.Append>
                                    <Button
                                        style={{backgroundColor: '#B84'}}
                                        variant="outline-light"
                                        className="px-4">
                                        Szukaj
                                    </Button>
                                </InputGroup.Append>
                            </InputGroup>
                            <div className="text-center">
                                <Button
                                    size="sm"
                                    onClick={() => this.setState({open: !this.state.open})}
                                    aria-controls="example-collapse-text"
                                    aria-expanded={this.state.open}
                                    variant="light"
                                >
                                    {!this.state.open && <small className="text-muted">
                                        Wyświetl filtry
                                        <FontAwesomeIcon icon="arrow-down" className="mx-1"/>
                                    </small>}
                                    {this.state.open && <small className="text-muted">
                                        Schowaj filtry
                                        <FontAwesomeIcon icon="arrow-up" className="mx-1"/>
                                    </small>}

                                </Button>
                            </div>
                            <div className="text-center">
                                <Collapse in={this.state.open}>
                                    <div id="example-collapse-text">
                                        Jakieś filtry
                                    </div>
                                </Collapse>
                            </div>
                        </div>
                    </Card.Header>
                    <div className="row m-4">
                        {this.state.page.content.map(notice =>
                            <NoticeboardItem
                                title={notice.title}
                                price={notice.price}
                                location={notice.location}
                                image={notice.primaryImage}
                                createdAt={notice.createdAt}
                                id={notice.id}
                                key={notice.id}
                            />
                        )}
                    </div>
                    <Pagination className="mx-auto small">
                        <Pagination.Prev/>
                        {this.state.page.number > 2 && this.state.page.number < this.state.page.totalPages - 1 && (
                            <span>
                                <Pagination.Item style={{color: '#B84'}}>{1}</Pagination.Item>
                                <Pagination.Ellipsis/>
                                <Pagination.Item>{this.state.page.number - 1}</Pagination.Item>
                                <Pagination.Item active>{this.state.page.number}</Pagination.Item>
                                <Pagination.Item>{this.state.page.number + 1}</Pagination.Item>
                                <Pagination.Ellipsis/>
                                <Pagination.Item>{this.state.page.totalPages}</Pagination.Item>
                            </span>
                        )
                        }
                        <Pagination.Next/>
                    </Pagination>
                </Card>
            </div>


        )
    };
}

export default HomePage;
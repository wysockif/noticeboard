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
                                    className="gold-glow"
                                    placeholder="Czego szukasz?"
                                />
                                <InputGroup.Append>
                                    <Button
                                        style={{backgroundColor: '#b78e56'}}
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
                                    aria-controls="collapse-text"
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
                            <Collapse in={this.state.open}>
                                <div id="collapse-text">
                                    <div className="mt-2 col-11 mx-auto">
                                        <form className="row g-3 justify-content-center">
                                            <div className="col-md-4">
                                                <label htmlFor="inputMinPrice" className="form-label">Cena od:</label>
                                                <input type="text" className="form-control" id="inputMinPrice"
                                                       placeholder="np. 20 zł"/>
                                            </div>
                                            <div className="col-md-4">
                                                <label htmlFor="inputMaxPrice" className="form-label">Cena do:</label>
                                                <input type="text" className="form-control" id="inputMaxPrice"
                                                       placeholder="np. 100 zł"/>
                                            </div>

                                            <div className="col-md-4">
                                                <label htmlFor="inputPagination"
                                                       className="form-label">Sortowanie</label>
                                                <select id="inputPagination" className="form-select">
                                                    <option value="0" defaultValue>Od najnowszych</option>
                                                    <option value="1">Od najstarszych</option>
                                                    <option value="2">Od najtańszych</option>
                                                    <option value="3">Od najdroższych</option>
                                                </select>
                                            </div>

                                            <div className="col-md-8">
                                                <label htmlFor="inputLocation"
                                                       className="form-label">Lokalizacja:</label>
                                                <input type="text" className="form-control" id="inputLocation"
                                                       placeholder="np. Warszawa"/>
                                            </div>


                                            <div className="col-md-4">
                                                <label htmlFor="inputPagination" className="form-label">Ilość na
                                                    stronie</label>
                                                <select id="inputSorting" className="form-select">
                                                    <option value="0" defaultValue>15</option>
                                                    <option value="1">30</option>
                                                </select>
                                            </div>
                                            <div className="col-12 text-center">
                                                <Button
                                                    style={{backgroundColor: '#b78e56'}}
                                                    variant="outline-light"
                                                    className="px-4 btn-sm">
                                                    Zastosuj
                                                </Button>
                                            </div>
                                        </form>
                                    </div>


                                </div>

                            </Collapse>
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
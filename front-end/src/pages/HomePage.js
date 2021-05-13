import React, {Component} from 'react';
import {Button, Card, Collapse, FormControl, InputGroup, Spinner} from 'react-bootstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import NoticeboardItem from "../components/NoticeboardItem";
import * as apiCalls from "../api/apiCalls";
import ButtonWithSpinner from "../components/ButtonWithSpinner";
import PaginationBar from "../components/PaginationBar";


class HomePage extends Component {
    state = {
        open: false,
        page: {
            content: [],
            number: 0,
            size: 12,
            totalPages: 1
        },
        requestParams: {
            minPrice: undefined,
            maxPrice: undefined,
            location: undefined,
            searched: undefined,
        },
        currentPage: 0,
        currentSize: 12,
        currentSort: 'createdAt,desc',
        searchingField: '',
        isSearching: false,
        isLoadingContent: true
    }

    componentDidMount() {
        this.loadNotices();
    }

    onSelectSorting = event => {
        this.setState({currentPage: 0, currentSort: event.target.value}, () => {
            this.loadNotices();
        });

    }

    onSelectPageSize = event => {
        this.setState({currentPage: 0, currentSize: event.target.value}, () => {
            this.loadNotices();
        });
    }

    onClickSearch = () => {
        this.setState({isSearching: true})
        let requestParams = {...this.state.requestParams};
        requestParams.searched = this.state.searchingField;
        this.setState({requestParams}, () => {
            this.loadNotices();
        });
    }

    onClickNext = () => {
        if (!this.state.page.last) {
            this.setState({currentPage: this.state.currentPage + 1}, () => {
                this.loadNotices();
            });
        }
    }
    onClickPrevious = () => {
        if (!this.state.page.first) {
            this.setState({currentPage: this.state.currentPage - 1}, () => {
                this.loadNotices();
            });
        }
    }

    onClickFirst = () => {
        this.setState({currentPage: 0}, () => {
            this.loadNotices();
        });
    }

    onClickLast = () => {
        this.setState({currentPage: this.state.page.totalPages - 1}, () => {
            this.loadNotices();
        });
    }
    loadNotices = () => {
        const {currentPage, currentSort, currentSize, requestParams} = this.state;
        this.setState({isLoadingContent: true})
        apiCalls.getNotices(currentPage, currentSort, currentSize, requestParams)
            .then(response => {
                this.setState({page: response.data, isSearching: false, isLoadingContent: false});
            })
            .catch(error => {

            });
    }
    onChangeMaxPrice = event => {
        let requestParams = {...this.state.requestParams};
        requestParams.maxPrice = event.target.value;
        this.setState({requestParams}, () => {
            this.loadNotices();
        });

    }
    onChangeMinPrice = event => {
        let requestParams = {...this.state.requestParams};
        requestParams.minPrice = event.target.value;
        this.setState({requestParams}, () => {
            this.loadNotices();
        });

    }


    onChangeLocation = event => {
        let requestParams = {...this.state.requestParams};
        requestParams.location = event.target.value.replace(' ', '+');
        this.setState({requestParams}, () => {
            this.loadNotices();
        });

    }

    onChangeSearched = event => {
        this.setState({searchingField: event.target.value});
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
                                    onChange={this.onChangeSearched}
                                />
                                <InputGroup.Append>
                                    <ButtonWithSpinner
                                        variant="outline-light"
                                        onClick={this.onClickSearch}
                                        ongoingApiCall={this.state.isSearching}
                                        content="Szukaj"
                                    />
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
                                            <div className="col-lg-8 row mt-3">
                                                <div className="col-sm-6 mt-2">
                                                    <label htmlFor="inputMinPrice" className="form-label">Cena
                                                        od:</label>
                                                    <input type="text" className="form-control" id="inputMinPrice"
                                                           placeholder="np. 20 zł" onBlur={this.onChangeMinPrice}/>
                                                </div>
                                                <div className="col-sm-6  mt-2">
                                                    <label htmlFor="inputMaxPrice" className="form-label">Cena
                                                        do:</label>
                                                    <input type="text" className="form-control" id="inputMaxPrice"
                                                           placeholder="np. 100 zł" onBlur={this.onChangeMaxPrice}/>
                                                </div>
                                                <div className="col-12  mt-2">
                                                    <label htmlFor="inputLocation"
                                                           className="form-label">Lokalizacja:</label>
                                                    <input type="text" className="form-control" id="inputLocation"
                                                           placeholder="np. Warszawa"
                                                           onBlur={this.onChangeLocation}/>
                                                </div>
                                            </div>

                                            <div className="col-lg-4 row mt-md-3">

                                                <div className="col-12  mt-2">
                                                    <label htmlFor="inputPagination" className="form-label">Ilość na
                                                        stronie</label>
                                                    <select id="inputSorting" className="form-select"
                                                            onChange={this.onSelectPageSize}>
                                                        <option value="12" defaultValue>12</option>
                                                        <option value="24">24</option>
                                                        <option value="36">36</option>
                                                    </select>
                                                </div>
                                                <div className="col-12  mt-2">
                                                    <label htmlFor="inputPagination"
                                                           className="form-label">Sortowanie</label>
                                                    <select id="inputPagination" className="form-select"
                                                            onChange={this.onSelectSorting}>
                                                        <option value="createdAt,desc" defaultValue>Od najnowszych
                                                        </option>
                                                        <option value="createdAt">Od najstarszych</option>
                                                        <option value="price">Od najtańszych</option>
                                                        <option value="price,desc">Od najdroższych</option>
                                                    </select>
                                                </div>
                                            </div>
                                        </form>
                                    </div>

                                </div>
                            </Collapse>
                        </div>
                        <div className="text-muted text-center mt-1">
                            {this.state.requestParams.searched &&
                            <span
                                className="badge bg-secondary  mx-1">Wyszukiwanie: {this.state.requestParams.searched}</span>}
                            {this.state.requestParams.minPrice &&
                            <span
                                className="badge bg-secondary mx-1">Cena od: {this.state.requestParams.minPrice} zł </span>}
                            {this.state.requestParams.maxPrice &&
                            <span
                                className="badge bg-secondary  mx-1">Cena do: {this.state.requestParams.maxPrice} zł </span>}
                            {this.state.requestParams.location &&
                            <span
                                className="badge bg-secondary  mx-1">Lokalizacja: {this.state.requestParams.location}</span>}
                        </div>
                    </Card.Header>
                    <div className="row m-4">
                        {this.state.isLoadingContent && <div className="text-center">
                            <Spinner animation="border" size="sm" role="status" className="ms-1">
                                <span className="sr-only">Loading...</span>
                            </Spinner>
                        </div>}
                        {!this.state.isLoadingContent && this.state.page.content.map(notice =>
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

                        {!this.state.isLoadingContent && (this.state.page.content.length < 1) &&
                        <div className="text-center">
                            Nie znaleziono ogłoszeń
                        </div>}
                    </div>
                    {!this.state.isLoadingContent && (this.state.page.totalPages > 1) &&
                    <PaginationBar
                        page={this.state.page}
                        onClickFirst={this.onClickFirst}
                        onClickLast={this.onClickLast}
                        onClickPrevious={this.onClickPrevious}
                        onClickNext={this.onClickNext}
                    />
                    }
                </Card>
            </div>


        )
    }
}

export default HomePage;
import React, {Component} from 'react';
import {Button, Card, Collapse, FormControl, InputGroup, Spinner} from 'react-bootstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import NoticeboardItem from "../components/NoticeboardItem";
import * as apiCalls from "../api/apiCalls";
import ButtonWithSpinner from "../components/ButtonWithSpinner";
import PaginationBar from "../components/PaginationBar";
import InputFilters from "../components/InputFilters";
import PageOptionsSelection from "../components/PageOptionsSelection";

class HomePage extends Component {
    _isMounted = false;

    state = {
        open: false,
        page: {
            content: [],
            number: 0,
            size: 12,
            totalPages: 1
        },
        minPriceInput: '',
        minPriceParam: '',
        minPriceError: '',
        maxPriceInput: '',
        maxPriceParam: '',
        maxPriceError: '',
        locationInput: '',
        locationParam: '',
        searchingInput: '',
        searchingParam: '',
        currentPage: 0,
        currentSize: 12,
        currentSort: 'createdAt,desc',
        isSearching: false,
        isLoadingContent: true,
        validationErrors: [],
        loadingError: false
    }

    componentWillUnmount() {
        this._isMounted = false;
    }

    componentDidMount() {
        this._isMounted = true;
        this.loadNotices();
    }


    loadNotices = () => {
        if (this._isMounted) {
            this.setState({isLoadingContent: true, loadingError: false})
            const {currentPage, currentSort, currentSize} = this.state;
            const {searchingParam, locationParam, minPriceParam, maxPriceParam} = this.state;
            const requestParams = {searchingParam, locationParam, minPriceParam, maxPriceParam};
            apiCalls.getNotices(currentPage, currentSort, currentSize, requestParams)
                .then(response => {
                    if (this._isMounted) {
                        this.setState({page: response.data, isSearching: false, isLoadingContent: false, title: true});
                    }
                })
                .catch(error => {
                    if (this._isMounted) {
                        this.setState({
                            loadingError: true, isSearching: false, isLoadingContent: false, page: {
                                content: [],
                            },
                        });
                    }
                });
        }
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
        const searchingInput = this.state.searchingInput.trim().replaceAll(' ', '+');
        this.setState({searchingParam: searchingInput, isSearching: true, currentPage: 0}, () => {
            this.loadNotices();
        });
    }

    handleKey = (e) => {
        if (e.key === 'Enter') {
            this.onClickSearch();
        }
    }

    validatePrice(input) {
        return /^[0-9]+(\.[0-9]{1,2})?$/.test(input) || input === '';
    }

    onChangeMinPrice = event => {
        const newMinPriceInputValue = event.target.value;
        if (newMinPriceInputValue.length < 15) {
            this.setState({minPriceInput: newMinPriceInputValue, minPriceError: ''});
        }
    }

    onBlurMinPrice = () => {
        const newMinPriceParam = this.state.minPriceInput.replace('zł', '').trim();
        if (this.state.minPriceParam !== newMinPriceParam) {
            if (!this.validatePrice(newMinPriceParam)) {
                this.setState({minPriceError: 'Niepoprawny format'});
            } else {
                this.setState({minPriceParam: newMinPriceParam, currentPage: 0}, () => {
                    this.loadNotices();
                });
            }
        }
    }

    onClickDeleteMinPrice = () => {
        this.setState({minPriceParam: '', minPriceInput: '', minPriceError: '', currentPage: 0}, () => {
            this.loadNotices();
        });
    }

    onChangeMaxPrice = event => {
        const newMaxPriceInputValue = event.target.value;
        if (newMaxPriceInputValue.length < 15) {
            this.setState({maxPriceInput: newMaxPriceInputValue, maxPriceError: ''});
        }
    }

    onBlurMaxPrice = () => {
        const newMaxPrice = this.state.maxPriceInput.replace('zł', '').trim();
        if (this.state.maxPriceParam !== newMaxPrice) {
            if (!this.validatePrice(newMaxPrice)) {
                this.setState({maxPriceError: 'Niepoprawny format'});
            } else {
                this.setState({maxPriceParam: newMaxPrice, currentPage: 0}, () => {
                    this.loadNotices();
                });
            }
        }
    }

    onClickDeleteMaxPrice = () => {
        this.setState({maxPriceParam: '', maxPriceInput: '', maxPriceError: '', currentPage: 0}, () => {
            this.loadNotices();
        });
    }

    onBlurLocation = () => {
        const newLocation = this.state.locationInput.trim().replace(' ', '+');
        if (this.state.locationParam !== newLocation) {
            this.setState({locationParam: newLocation, currentPage: 0}, () => {
                this.loadNotices();
            });
        }
    }

    onClickDeleteLocation = () => {
        this.setState({locationParam: '', locationInput: '', currentPage: 0}, () => {
            this.loadNotices();
        });
    }

    onChangeLocation = event => {
        const location = event.target.value;
        if (location.length < 50) {
            this.setState({locationInput: location});
        }
    }


    onChangeSearchingInput = event => {
        const searched = event.target.value;
        if (searched.length < 60) {
            this.setState({searchingInput: searched});
        }
    }

    onClickDeleteSearching = () => {
        this.setState({searchingInput: '', searchingParam: '', currentPage: 0}, () => {
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

    render() {
        return (
            <div data-testid="homepage" className="mb-5 mt-4">
                <Card style={{marginBottom: "90px"}} className="mh-home-card">
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
                                    onChange={this.onChangeSearchingInput}
                                    value={this.state.searchingInput}
                                    onKeyPress={this.handleKey}
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
                                        <form className="row g-3 justify-content-center align-items-start">
                                            <InputFilters
                                                onChangeLocation={this.onChangeLocation}
                                                onBlurLocation={this.onBlurLocation}
                                                location={this.state.locationInput}
                                                onChangeMaxPrice={this.onChangeMaxPrice}
                                                onBlurMaxPrice={this.onBlurMaxPrice}
                                                maxPrice={this.state.maxPriceInput}
                                                maxPriceError={this.state.maxPriceError}
                                                onChangeMinPrice={this.onChangeMinPrice}
                                                onBlurMinPrice={this.onBlurMinPrice}
                                                minPrice={this.state.minPriceInput}
                                                minPriceError={this.state.minPriceError}
                                            />
                                            <PageOptionsSelection
                                                onSelectPageSize={this.onSelectPageSize}
                                                onSelectSorting={this.onSelectSorting}
                                            />
                                        </form>
                                    </div>
                                </div>
                            </Collapse>
                        </div>
                        <div className="text-muted text-center mt-1 col-11 mx-auto">
                            {this.state.searchingParam &&
                            <span className="badge bg-secondary mx-1">
                                <span className="d-inline-block text-truncate text-nowrap" style={{maxWidth: '210px'}}>
                                    Wyszukiwanie: {this.state.searchingParam}
                                </span>
                                <span className="ms-2" style={{cursor: 'pointer'}}
                                      onClick={this.onClickDeleteSearching}>X</span>
                            </span>}
                            {this.state.minPriceParam &&
                            <span
                                className="badge bg-secondary mx-1 align-content-center">
                                      <span className="d-inline-block text-truncate text-nowrap"
                                            style={{maxWidth: '210px'}}>
                                Cena od: {this.state.minPriceParam} zł
                                      </span>
                                <span className="ms-2" style={{cursor: 'pointer'}}
                                      onClick={this.onClickDeleteMinPrice}>X</span>
                            </span>}
                            {this.state.maxPriceParam &&
                            <span
                                className="badge bg-secondary mx-1">
                                <span
                                    className="d-inline-block text-truncate text-nowrap"
                                    style={{maxWidth: '210px'}}>
                                Cena do: {this.state.maxPriceParam} zł
                                   </span>
                                <span className="ms-2" style={{cursor: 'pointer'}}
                                      onClick={this.onClickDeleteMaxPrice}>X</span>
                            </span>}
                            {this.state.locationParam &&
                            <span className="badge bg-secondary  mx-1">
                                <span
                                    className="d-inline-block text-truncate text-nowrap"
                                    style={{maxWidth: '210px'}}>
                                Lokalizacja: {this.state.locationParam.replaceAll('+', '')}
                                </span>
                                <span className="ms-2" style={{cursor: 'pointer'}}
                                      onClick={this.onClickDeleteLocation}>X</span>
                            </span>}
                        </div>
                    </Card.Header>
                    <div>
                        <div className="row m-3 mt-2">
                            {this.state.isLoadingContent && <div className="text-center mt-3 ">
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
                            {this.state.loadingError &&
                            <div className="text-center text-muted mt-2">
                                Wystąpił błąd podczas ładowania ogłoszeń. Spróbuj ponownie później.
                            </div>}
                            {!this.state.loadingError && !this.state.isLoadingContent && (this.state.page.content.length < 1) &&
                            <div className="text-center text-muted mt-3 row align-items-center">
                                <div className="col ms-4">Nie znaleziono ogłoszeń</div>
                            </div>}
                        </div>
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

HomePage.defaultProps = {
    location: {
        state: {},
        key: 0
    }
}

export default HomePage;
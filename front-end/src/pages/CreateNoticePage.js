import React, {Component} from 'react';
import {Card, Container} from 'react-bootstrap';
import {connect} from 'react-redux';
import {Redirect} from 'react-router';
import ButtonWithSpinner from '../components/ButtonWithSpinner';
import BasicInformation from '../components/notice-form/BasicInformation';
import CreateNoticePageContact from '../components/notice-form/ContactInformation';
import DescriptionForm from '../components/notice-form/DescriptionForm';
import ImagesUpload from '../components/notice-form/ImagesUpload';
import * as apiCalls from '../api/apiCalls';

class CreateNoticePage extends Component {

    state = {
        title: '',
        location: '',
        price: '',
        description: '',
        ongoingApiCall: false,
        errors: undefined
    }

    imagesComponent = React.createRef();

    onClickSubmit = () => {
        const primaryImage = this.imagesComponent.current.state.primaryImage.split(',')[1];
        const secondaryImage = this.imagesComponent.current.state.secondaryImage.split(',')[1];
        const tertiaryImage = this.imagesComponent.current.state.tertiaryImage.split(',')[1];
        const price = this.state.price
            .replace('zł', '').replace('zl', '')
            .replaceAll(' ','');
        const title = this.state.title.trim();
        const location = this.state.location.trim();
        const description = this.state.description.trim();
        const notice = {
            title,
            location,
            price,
            description,
            primaryImage,
            secondaryImage,
            tertiaryImage
        }
        this.setState({ongoingApiCall: true})
        apiCalls.postNotice(notice)
            .then(response => {
                this.props.history.push(`/notice/${response.data}`);
            })
            .catch(apiError => {
                let errors = {...this.state.errors};
                if (apiError.response.data && apiError.response.data.validationErrors) {
                    errors = {...apiError.response.data.validationErrors}
                }
                this.setState({ongoingApiCall: false, errors});
                window.scrollTo(0, 75);
            });
    }

    onChangeTitle = event => {
        const errors = {...this.state.errors};
        delete errors.title;
        this.setState({title: event.target.value, errors});
    }

    onChangeLocation = event => {
        const errors = {...this.state.errors};
        delete errors.location;
        this.setState({location: event.target.value, errors});
    }

    onChangePrice = event => {
        const errors = {...this.state.errors};
        delete errors.price;
        this.setState({price: event.target.value, errors});
    }

    onChangeDescription = event => {
        const errors = {...this.state.errors};
        delete errors.description;
        const value = event.target.value;
        if (value.length > 2000) {
            errors.description = 'Opis ogłoszenia nie może być dłuższy niż 2000 znaków';
            this.setState({errors});
            return;
        }
        this.setState({description: event.target.value, errors});
    }


    render() {
        return (
            <Container data-testid="createnoticepage" className="my-3">
                {!this.props.isLoggedIn && <Redirect to="/login"/>}
                <Card style={{marginBottom: "95px"}}>
                    <Card.Header className="text-center">
                        <h5 className="my-2">Dodaj ogłoszenie </h5>
                    </Card.Header>
                    <Card.Body>
                        <BasicInformation
                            title={this.state.title}
                            onChangeTitle={this.onChangeTitle}
                            titleError={this.state.errors && this.state.errors.title}
                            location={this.state.location}
                            onChangeLocation={this.onChangeLocation}
                            locationError={this.state.errors && this.state.errors.location}
                            price={this.state.price}
                            onChangePrice={this.onChangePrice}
                            priceError={this.state.errors && this.state.errors.price}
                        />
                        <CreateNoticePageContact email={this.props.email}/>
                        <DescriptionForm
                            description={this.state.description}
                            onChangeDescription={this.onChangeDescription}
                            length={this.state.description.length}
                            descriptionError={this.state.errors && this.state.errors.description}
                        />
                        <ImagesUpload
                            ref={this.imagesComponent}
                            errors={this.state.errors}
                        />
                        <div className="text-center my-2 mt-4">
                            <ButtonWithSpinner
                                content="Dodaj ogłoszenie"
                                onClick={this.onClickSubmit}
                                ongoingApiCall={this.state.ongoingApiCall}
                            />
                        </div>
                    </Card.Body>
                </Card>
            </Container>
        );
    }
}

const mapStateToProps = state => {
    return {
        isLoggedIn: state.isLoggedIn,
        email: state.email
    }
}

export default connect(mapStateToProps)(CreateNoticePage);
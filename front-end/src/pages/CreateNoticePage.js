import React, {Component} from 'react';
import {Card, Container} from 'react-bootstrap';
import {connect} from 'react-redux';
import {Redirect} from 'react-router';
import ButtonWithSpinner from '../components/ButtonWithSpinner';
import BasicInformation from '../components/create-notice-page/BasicInformation';
import CreateNoticePageContact from '../components/create-notice-page/ContactInformation';
import DescriptionForm from '../components/create-notice-page/DescriptionForm';
import ImagesUpload from '../components/create-notice-page/ImagesUpload';
import KeywordsInput from '../components/create-notice-page/KeywordsInput';
import * as apiCalls from '../api/apiCalls';

class CreateNoticePage extends Component {

    state = {
        title: 'Sprzedam Opla',
        location: 'Warszawa',
        price: '2000',
        description: 'Suspendisse laoreet felis et leo ullamcorper tincidunt. Sed sapien risus, pulvinar vitae ligula eget, finibus aliquam mi. Pellentesque metus mauris, feugiat vitae sapien vel, efficitur pretium ante. Sed id lacinia est. Nam in quam dapibus, egestas tellus id, ultrices tortor. Integer molestie in dui eget ornare. Nam et nunc id odio ullamcorper cursus in quis lectus. Mauris convallis lacinia lorem, sit amet rutrum nisl bibendum vitae. Vestibulum ullamcorper elementum lectus, vel pulvinar ante viverra id. ',
        ongoingApiCall: false,
        errors: undefined
    }

    keywordsComponent = React.createRef();
    imagesComponent = React.createRef();

    onClickSubmit = () => {
        const keywords = this.keywordsComponent.current.state.keywords;
        const primaryImage = this.imagesComponent.current.state.primaryImage.split(',')[1];
        const secondaryImage = this.imagesComponent.current.state.secondaryImage.split(',')[1];
        const tertiaryImage = this.imagesComponent.current.state.tertiaryImage.split(',')[1];
        const price = this.state.price.replace('zł', '').trim();
        const title = this.state.title.trim();
        const location = this.state.location.trim();
        const description = this.state.description.trim();
        const notice = {
            title,
            location,
            price,
            description,
            keywords,
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
        this.setState({description: event.target.value, errors});
    }


    render() {
        return (
            <Container data-testid="createnoticepage" className="my-3">
                {!this.props.isLoggedIn && <Redirect to="/login"/>}
                <Card>
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
                        <KeywordsInput
                            ref={this.keywordsComponent}
                            errors={this.state.errors && this.state.errors.keywords}
                        />
                        <ImagesUpload
                            ref={this.imagesComponent}
                            errors={this.state.errors}
                        />
                        <div className="text-center my-2">
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
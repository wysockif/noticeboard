import React, {Component} from 'react';
import {Card} from "react-bootstrap";
import SimpleImage from "./SingleImage";

export class ImagesUpload extends Component {
    state = {
        primaryImage: '',
        secondaryImage: '',
        tertiaryImage: ''
    }

    onSelectPrimaryImage = event => {
        if (event.target.files.length > 0) {
            const file = event.target.files[0];
            if (file.type === 'image/jpg' || file.type === 'image/jpeg' || file.type === 'image/png') {
                let fileReader = new FileReader();
                fileReader.onloadend = () => {
                    this.setState({primaryImage: fileReader.result});
                }
                fileReader.readAsDataURL(file);
            } else {
                // let errors = {...this.state.errors};
                // this.setState({
                //     errors: {
                //         ...errors,
                //         profileImage: 'Wybrany plik musi posiadać format png, jpg lub jpeg'
                //     }
                // });
            }
        }
    }

    onSelectSecondaryImage = event => {
        if (event.target.files.length > 0) {
            const file = event.target.files[0];
            if (file.type === 'image/jpg' || file.type === 'image/jpeg' || file.type === 'image/png') {
                let fileReader = new FileReader();
                fileReader.onloadend = () => {
                    this.setState({secondaryImage: fileReader.result});
                }
                fileReader.readAsDataURL(file);
            } else {
                // let errors = {...this.state.errors};
                // this.setState({
                //     errors: {
                //         ...errors,
                //         profileImage: 'Wybrany plik musi posiadać format png, jpg lub jpeg'
                //     }
                // });
            }
        }
    }

    onSelectTertiaryImage = event => {
        if (event.target.files.length > 0) {
            const file = event.target.files[0];
            if (file.type === 'image/jpg' || file.type === 'image/jpeg' || file.type === 'image/png') {
                let fileReader = new FileReader();
                fileReader.onloadend = () => {
                    this.setState({tertiaryImage: fileReader.result});
                }
                fileReader.readAsDataURL(file);
            } else {
                // let errors = {...this.state.errors};
                // this.setState({
                //     errors: {
                //         ...errors,
                //         profileImage: 'Wybrany plik musi posiadać format png, jpg lub jpeg'
                //     }
                // });
            }
        }
    }

    render() {
        return (
            <Card className="mt-2">
                <div className="col-11 mx-auto row">
                    <Card.Title className="m-3 mb-1"><small>Zdjęcia</small></Card.Title>
                    <small className="mx-3 text-muted">Kolejność zdjęć ma znaczenie.</small>
                    <SimpleImage
                        content="Zdjęcie pierwszoplanowe"
                        image={this.state.primaryImage}
                        onSelectImage={this.onSelectPrimaryImage}
                    />
                    <SimpleImage
                        content="Zdjęcie drugoplanowe"
                        image={this.state.secondaryImage}
                        onSelectImage={this.onSelectSecondaryImage}
                    />
                    <SimpleImage
                        content="Zdjęcie trzecioplanowe"
                        image={this.state.tertiaryImage}
                        onSelectImage={this.onSelectTertiaryImage}
                    />
                </div>
            </Card>
        );
    };
}


export default ImagesUpload;

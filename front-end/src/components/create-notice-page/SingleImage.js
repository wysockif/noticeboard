import React, {Component} from 'react';
import {Card, FormControl, Image} from "react-bootstrap";
import defaultNoticeImage from "../../assets/default-notice-image.jpg";

class SimpleImage extends Component {
    state = {
        selectedImage: ''
    }

    onImageSelect = event => {
        if (event.target.files.length > 0) {
            const file = event.target.files[0];
            if (file.type === 'image/jpg' || file.type === 'image/jpeg' || file.type === 'image/png') {
                let fileReader = new FileReader();
                fileReader.onloadend = () => {
                    this.setState({selectedImage: fileReader.result});
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
        const img = this.state.selectedImage ? this.state.selectedImage : defaultNoticeImage;

        return (
            <div className="col-12 col-sm-11 col-md-9 col-lg-5 col-xl-4 mx-auto mb-3 mx-1">
                <Card.Text as="div" className="mx-3 my-1">
                    <div>
                        <Image src={img} className="croppedImage mx-auto d-inline-block"/>
                    </div>
                    <div className="my-2">
                        <div className="text-center">{this.props.content}</div>
                        <div className="custom-file mb-3 mx-auto mt-2">
                            <input className="form-control" type="file" id="formFile" onChange={this.onImageSelect}/>
                            <FormControl.Feedback type="invalid" className="text-center">
                                Coś tam
                            </FormControl.Feedback>
                        </div>
                    </div>
                </Card.Text>
            </div>
        );
    }
}

export default SimpleImage;

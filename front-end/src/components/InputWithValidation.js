import React from 'react';
import { InputGroup, FormControl } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';


const InputWithValidation = props => {
    const type = props.type ? props.type : 'text';
    return (
        <InputGroup className="mb-3">
            <InputGroup.Prepend style={{ minWidth: '200px' }}>
                <InputGroup.Text>
                    <FontAwesomeIcon icon={props.icon} className="my-2 me-2" /> {props.label}
                </InputGroup.Text>
            </InputGroup.Prepend>
            <FormControl
                placeholder={props.placeholder}
                value={props.value}
                onChange={props.onChange}
                type={type}
                isInvalid={props.hasError}
                isValid={props.isCorrect && true}
            />
            <FormControl.Feedback type="invalid" className="text-center">
                {props.hasError && props.error}
            </FormControl.Feedback>

        </InputGroup>
    );
}

InputWithValidation.defaultProps = {
    onChange: () => { }
};

export default InputWithValidation;
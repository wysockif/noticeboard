import { render } from '@testing-library/react';
import InputWithValidation from './InputWithValidation';

describe('Layout', () => {
    it('has input', () => {
        // given
        const { container } = render(<InputWithValidation />);
        // when  
        const input = container.querySelector('input');
        // then
        expect(input).toBeInTheDocument();
    });

    it('displays the label provided in props', () => {
        // given
        const { queryByText } = render(<InputWithValidation label="Test label" />);
        // when
        const label = queryByText('Test label');
        // then
        expect(label).toBeInTheDocument();
    });

    it('does not display the label when no label provided in props', () => {
        // given
        const { container } = render(<InputWithValidation />);
        // when
        const label = container.querySelector('label');
        // then
        expect(label).not.toBeInTheDocument();
    });

    it('has text type for input when type is not provided as prop', () => {
        // given
        const { container } = render(<InputWithValidation />);
        // when
        const input = container.querySelector('input');
        // then
        expect(input.type).toBe('text');
    });

    it('has password type for input when password type is provided as prop', () => {
        // given
        const { container } = render(<InputWithValidation type="password" />);
        // when
        const input = container.querySelector('input');
        // then
        expect(input.type).toBe('password');
    });

    it('displays placeholder when it is provided as prop', () => {
        // given
        const { container } = render(<InputWithValidation placeholder="Test placeholder" />);
        // when
        const input = container.querySelector('input');
        // then
        expect(input.placeholder).toBe('Test placeholder');
    });

    it('displays the error text when it is provided', () => {
        // given
        const { queryByText } = render(<InputWithValidation hasError={true} error="Cannot be null" />);
        // when
        const errorText = queryByText('Cannot be null');
        // then
        expect(errorText).toBeInTheDocument();
    });

    it('does not display the error text when it hasError not provided', () => {
        // given
        const { queryByText } = render(<InputWithValidation error="Cannot be null" />);
        // when
        const errorText = queryByText('Cannot be null');
        // then
        expect(errorText).not.toBeInTheDocument();
    });
});

console.error = () => { }

interface ErrorBoxProps {
    error: string
}
export const ErrorBox = ({error}: ErrorBoxProps) => {

     return (
        <div className="bg-red-400 rounded py-4 px-12">
            {error}
        </div>
     )
}
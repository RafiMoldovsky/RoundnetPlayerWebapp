interface pageProps{
  params: {name: string}
}

export default function PlayerPage({params}: pageProps){
  return <div>{params.name}</div>
}
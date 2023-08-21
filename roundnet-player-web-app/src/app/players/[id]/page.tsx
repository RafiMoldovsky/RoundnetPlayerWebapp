'use client';
import { useSearchParams } from 'next/navigation';
import { players, playerInterface, isAPlayer } from '../../../../data/players';

interface pageProps{
  params: {id: string}
}

export default function PlayerPage({params}: pageProps){
  const player = players.get(params.id);
  if(!isAPlayer(player)) {
    return <div>PLAYER NOT FOUND!</div>;
  }
  return (
    <>
      <div>{player.player_name}</div>
    </>
  )
}
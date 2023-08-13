import Link from "next/link"

export default function Navbar() {
  return (
    <>
      <Link href={"/"}>
        <button>
          Home
        </button>
      </Link>
      <Link href={"/tournaments"}>
        <button>
          Tournaments
        </button>
      </Link>
      <Link href={"/players"}>
        <button>
          Players
        </button>
      </Link>
    </>
  )
}